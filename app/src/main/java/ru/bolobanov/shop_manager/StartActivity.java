package ru.bolobanov.shop_manager;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
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


    //создаем новый Item и открываем его в edtit fragment
    @Override
    public Item createItem() {
        if (isTablet) {
            EditFragment editFragment = (EditFragment) getFragmentManager().findFragmentById(R.id.fragment_edit);
            editFragment.createItem();
        } else {

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
