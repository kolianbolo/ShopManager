package ru.bolobanov.shop_manager.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
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

    private Item mOldItem;

    @ViewById
    public EditText editName;

    @ViewById
    public EditText editPrice;

    @ViewById
    public EditText editNumber;

    private OnItemSaveListener mCallback;

    @AfterViews
    public void init() {
        mCallback = (OnItemSaveListener) getActivity();
        final Intent receivedIntent = getActivity().getIntent();
        if (receivedIntent != null) {
            Item item = receivedIntent.getParcelableExtra(Constants.ITEM_KEY);
            if (item == null) {
                createItem();
            } else {
                editItem(item);
            }
        }

    }


    public void editItem(Item pItem) {
        clear();
        mOldItem = pItem;
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
        //наэтом все - ожидаем ввода данных и нажатия на "применить"
    }

    //если удаляемы элемент прямо сейчас редактируется - чистим
    //только для планшета
    public void delteteItem(Item pItem) {
        if (mOldItem != null) {
            if (pItem.mId == mOldItem.mId) {
                clear();
                mOldItem = null;
            }
        }

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
        Toast.makeText(getActivity(), getString(R.string.success_create), Toast.LENGTH_LONG).show();
    }

    private boolean verify() {
        if (TextUtils.isEmpty(editName.getText()) || TextUtils.isEmpty(editPrice.getText()) || TextUtils.isEmpty(editNumber.getText())) {
            return false;
        }
        return true;
    }
}
