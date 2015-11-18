package ru.bolobanov.shop_manager;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import ru.bolobanov.shop_manager.database.ShopDatabaseHelper;
import ru.bolobanov.shop_manager.fragments.EditFragment;
import ru.bolobanov.shop_manager.fragments.GoodsListFragment;


@EActivity(R.layout.a_start)
public class StartActivity extends AppCompatActivity implements OnItemChangeListener, OnItemSaveListener {

    private ShopDatabaseHelper mDatabaseHelper;

    public static boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseHelper = new ShopDatabaseHelper(this);
    }

    @AfterViews
    protected void init() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentById(R.id.fragment_edit) != null) {
            isTablet = true;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){

            if(requestCode == Constants.CREATE_REQUEST){
                Item newItem = data.getParcelableExtra(Constants.ITEM_KEY);
                saveItem(newItem, true);
            }else if (requestCode == Constants.EDIT_REQUEST){
                Item newItem = data.getParcelableExtra(Constants.ITEM_KEY);
                saveItem(newItem, false);
            }
        }
    }

    //создаем новый Item и открываем его в edtit fragment
    @Override
    public Item createItem() {
        if (isTablet) {
            EditFragment editFragment = (EditFragment) getFragmentManager().findFragmentById(R.id.fragment_edit);
            editFragment.createItem();
        } else {
            Intent intent = new Intent(this, EditActivity_.class);
            intent.putExtra(Constants.CREATE_KEY, true);
            startActivityForResult(intent, Constants.CREATE_REQUEST);
        }
        return null;
    }

    //нужно открыть существующий tem для редактирования
    @Override
    public void editItem(Item pItem) {
        if (isTablet) {
            EditFragment editFragment = (EditFragment) getFragmentManager().findFragmentById(R.id.fragment_edit);
            editFragment.editItem(pItem);
        } else {
            Intent intent = new Intent(this, EditActivity_.class);
            intent.putExtra(Constants.ITEM_KEY, pItem);
            startActivityForResult(intent, Constants.EDIT_REQUEST);
        }
    }

    //проверим не удаляемый ли элемент сейчас редактируется
    @Override
    public void deleteItem(final Item pItem) {
        if (isTablet) {
            EditFragment editFragment = (EditFragment) getFragmentManager().findFragmentById(R.id.fragment_edit);
            editFragment.delteteItem(pItem);
        }
    }

    //этот метод дергает editFragment сам или через intent
    @Override
    public void saveItem(Item pItem, final boolean isNewItem) {
        if (isNewItem) {
            final FragmentManager fragmentManager = getFragmentManager();
            final GoodsListFragment listFragment = (GoodsListFragment) fragmentManager.findFragmentById(R.id.fragment_list);
            listFragment.addItemInList(pItem);
        } else {
            final FragmentManager fragmentManager = getFragmentManager();
            final GoodsListFragment listFragment = (GoodsListFragment) fragmentManager.findFragmentById(R.id.fragment_list);
            listFragment.changeItemInList(pItem);
        }
    }
}
