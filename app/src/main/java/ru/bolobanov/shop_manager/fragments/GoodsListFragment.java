package ru.bolobanov.shop_manager.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import ru.bolobanov.shop_manager.Item;
import ru.bolobanov.shop_manager.OnItemChangeListener;
import ru.bolobanov.shop_manager.R;
import ru.bolobanov.shop_manager.adapters.GoodsAdapter;
import ru.bolobanov.shop_manager.database.DatabaseService;

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

    private final String LAYOUT_STATE_KEY = "layout_state";
    private final String ADAPTER_ITEMS_KEY = "items";
    private final String ADAPTER_SCALE_KEY = "scale";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCallback = (OnItemChangeListener) getActivity();
        final GoodsAdapter adapter;
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState == null) {
            adapter = new GoodsAdapter(getActivity(), this);
        } else {
            final ArrayList<Item> items = savedInstanceState.getParcelableArrayList(ADAPTER_ITEMS_KEY);
            final int scale = savedInstanceState.getInt(ADAPTER_SCALE_KEY);
            adapter = new GoodsAdapter(getActivity(), this, scale, items);
            list.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(LAYOUT_STATE_KEY));
        }
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1)) {
                    //докрутили до самого низа, подгружаем следующую страницу
                    GoodsAdapter adapter = (GoodsAdapter) list.getAdapter();
                    adapter.loadNext();
                }
            }

        });
        list.setAdapter(adapter);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        list.setItemAnimator(itemAnimator);
        super.onActivityCreated(savedInstanceState);

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        Parcelable mRecycleState = list.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(LAYOUT_STATE_KEY, mRecycleState);
        outState.putParcelableArrayList(ADAPTER_ITEMS_KEY, ((GoodsAdapter) list.getAdapter()).getItems());
        outState.putInt(ADAPTER_SCALE_KEY, ((GoodsAdapter) list.getAdapter()).getScale());
        super.onSaveInstanceState(outState);
    }
}