package ru.bolobanov.shop_manager.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import ru.bolobanov.shop_manager.Item;
import ru.bolobanov.shop_manager.OnItemChangeListener;
import ru.bolobanov.shop_manager.R;
import ru.bolobanov.shop_manager.adapters.GoodsAdapter;
import ru.bolobanov.shop_manager.database.DatabaseService;
import ru.bolobanov.shop_manager.database.ShopDatabaseHelper;

/**
 * Created by Bolobanov Nikolay on 10.11.15.
 */
@EFragment(R.layout.f_list)
public class GoodsListFragment extends Fragment {

    @ViewById
    public RecyclerView list;

    private OnItemChangeListener mCallback;

    final int MENU_EDIT = 1;
    final int MENU_DELETE = 2;

    @AfterViews
    public void init() {
        mCallback = (OnItemChangeListener) getActivity();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        final GoodsAdapter adapter = new GoodsAdapter(getActivity(), this);
        list.setAdapter(adapter);
        list.setLayoutManager(layoutManager);
        list.setItemAnimator(itemAnimator);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1)) {
                    onScrolledToBottom();
                }
            }

            public void onScrolledToBottom() {
                GoodsAdapter adapter = (GoodsAdapter) list.getAdapter();
                adapter.loadNext();
            }
        });
    }

    //этот метод вызывает адаптер
    public void changeItem(final Item pItem) {
        mCallback.editItem(pItem);

    }

    //этот метод вызывает адаптер
    public void deleteItem(final Item pItem) {
        mCallback.deleteItem(pItem);
        final int deleted = DatabaseService.getInstance(getActivity()).deleteGood(pItem);
        Toast.makeText(getActivity(), new StringBuilder().append(getString(R.string.delete)).
                append(" ").append(deleted).toString(), Toast.LENGTH_LONG).show();
    }

    public void addItemInList(Item pItem) {
        GoodsAdapter adapter = (GoodsAdapter) list.getAdapter();
        adapter.addItem(pItem);

    }

    public void changeItemInList(Item pItem) {
        GoodsAdapter adapter = (GoodsAdapter) list.getAdapter();
        adapter.changeItem(pItem);
    }

    @Click(R.id.fab)
    public void fabClick() {
        //просто просим активити создать новый
        mCallback.createItem();
    }
}
