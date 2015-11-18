package ru.bolobanov.shop_manager.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import ru.bolobanov.shop_manager.Constants;
import ru.bolobanov.shop_manager.Item;
import ru.bolobanov.shop_manager.OnItemSaveListener;
import ru.bolobanov.shop_manager.R;
import ru.bolobanov.shop_manager.database.DatabaseService;

/**
 * Created by Bolobanov Nikolay on 10.11.15.
 */

@EFragment(R.layout.f_edit)
public class EditFragment extends Fragment {

    //сохраняем значения, введенные в поля и mOldIte
    private final String NAME_KEY = "name_key";
    private final String PRICE_KEY = "price_key";
    private final String NUMBER_KEY = "number_key";
    private final String OLD_ITEM_KEY = "old_item_key";
    private final String STUB_KEY = "stub_key";

    private Item mOldItem;

    @ViewById
    public EditText editName;

    @ViewById
    public EditText editPrice;

    @ViewById
    public EditText editNumber;

    @ViewById
    public LinearLayout editLinear;

    @ViewById
    public LinearLayout stubLinear;


    private OnItemSaveListener mCallback;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mCallback = (OnItemSaveListener) getActivity();
        if (savedInstanceState != null) {
            mOldItem = savedInstanceState.getParcelable(OLD_ITEM_KEY);
            editName.setText(savedInstanceState.getCharSequence(NAME_KEY));
            editPrice.setText(savedInstanceState.getCharSequence(PRICE_KEY));
            editNumber.setText(savedInstanceState.getCharSequence(NUMBER_KEY));
            if (savedInstanceState.getBoolean(STUB_KEY, false)) {
                showStub();
            } else {
                hideStub();
            }
        }
        final Intent receivedIntent = getActivity().getIntent();
        if (receivedIntent != null) {
            Item item = receivedIntent.getParcelableExtra(Constants.ITEM_KEY);
            if (item != null) {
                editItem(item);
            } else if (receivedIntent.getBooleanExtra(Constants.CREATE_KEY, false)) {
                createItem();
            }
        }

        super.onActivityCreated(savedInstanceState);
    }

    public void editItem(Item pItem) {
        hideStub();
        editName.setText(pItem.mName);
        editPrice.setText(String.valueOf(pItem.mPrice));
        editNumber.setText(String.valueOf(pItem.mNumber));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.edit);
        mOldItem = pItem;
    }

    public void createItem() {
        mOldItem = null;
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.create);
        clear();
        hideStub();
        //наэтом все - ожидаем ввода данных и нажатия на "применить"
    }

    //если удаляемы элемент прямо сейчас редактируется - чистим
    //только для планшета
    public void delteteItem(Item pItem) {
        if (mOldItem != null) {
            if (pItem.mId == mOldItem.mId) {
                clear();
                showStub();
                mOldItem = null;
            }
        }
    }

    private void showStub() {
        stubLinear.setVisibility(View.VISIBLE);
        editLinear.setVisibility(View.GONE);
    }

    private void hideStub() {
        stubLinear.setVisibility(View.GONE);
        editLinear.setVisibility(View.VISIBLE);
    }

    private void clear() {
        editName.setText(null);
        editPrice.setText(null);
        editNumber.setText(null);
    }

    @Click(R.id.applyButton)
    public void apply() {
        if (!verify()) {
            Toast.makeText(getActivity(), getString(R.string.full_fill), Toast.LENGTH_LONG).show();
            return;
        }
        Item newItem;
        if (mOldItem == null) {
            newItem = new Item(0, editName.getText().toString(), Integer.valueOf(editPrice.getText().toString()),
                    Integer.valueOf(editNumber.getText().toString()));
            newItem = DatabaseService.getInstance(getActivity()).saveGood(newItem);
            mCallback.saveItem(newItem, true);
        } else {
            newItem = new Item(mOldItem.mId, editName.getText().toString(), Integer.valueOf(editPrice.getText().toString()),
                    Integer.valueOf(editNumber.getText().toString()));
            DatabaseService.getInstance(getActivity()).saveGood(newItem);
            mCallback.saveItem(newItem, false);
            mOldItem = null;
        }
        clear();
        showStub();
        Toast.makeText(getActivity(), getString(R.string.success_create), Toast.LENGTH_LONG).show();
    }

    private boolean verify() {
        if (TextUtils.isEmpty(editName.getText()) || TextUtils.isEmpty(editPrice.getText()) || TextUtils.isEmpty(editNumber.getText())) {
            return false;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        outState.putBoolean(STUB_KEY, stubLinear.getVisibility() == View.VISIBLE);
        outState.putCharSequence(NAME_KEY, editName.getText());
        outState.putCharSequence(PRICE_KEY, editPrice.getText());
        outState.putCharSequence(NUMBER_KEY, editNumber.getText());
        outState.putParcelable(OLD_ITEM_KEY, mOldItem);
        super.onSaveInstanceState(outState);
    }
}
