package ru.bolobanov.shop_manager.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;


import ru.bolobanov.shop_manager.Item;
import ru.bolobanov.shop_manager.R;
import ru.bolobanov.shop_manager.database.DatabaseService;
import ru.bolobanov.shop_manager.fragments.GoodsListFragment;

/**
 * Created by Bolobanov Nikolay on 12.11.15.
 */

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> implements View.OnClickListener, View.OnLongClickListener, DialogInterface.OnClickListener {

    //размер подгружаемого изначально окна
    public final int START_SIZE = 26;
    //сколько элементов погружается при достижении края
    public final int PAGE_SIZE = 14;

    public final int EDIT = 0;
    public final int DELETE = 1;

    private final List<Item> mItems = new LinkedList<Item>();

    private final LayoutInflater mInflater;

    private final Context mContext;

    private final GoodsListFragment mFragment;

    private Dialog mDialog;

    //храним id последнего элемента для которого вызывали диалог
    private long mIdForDialog = -1;

    //сколько есть записей в таблице
    public int scale;

    public GoodsAdapter(final Context pContext, final GoodsListFragment pFragment) {
        mContext = pContext;
        mFragment = pFragment;
        mInflater = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scale = DatabaseService.getInstance(mContext).getGoodsSize();
        final Item[] itemArray = DatabaseService.getInstance(mContext).getGoods(0, START_SIZE);
        for (int i = 0; i < START_SIZE; i++) {
            if (itemArray[i] != null) {
                mItems.add(itemArray[i]);
            }
        }
        //если данных слишком много - добавляем progressbar
        if (START_SIZE < scale) {
            mItems.add(null);
        }
    }

    private void showDialog(long pItemId) {
        mIdForDialog = pItemId;
        if (mDialog == null) {
            mDialog = onCreateDialog();
        }
        mDialog.show();
    }

    private Dialog onCreateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(R.array.string_array_menu, this);
        builder.setCancelable(true);
        return builder.create();
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(GoodsViewHolder holder, int position) {
        if (mItems.get(position) == null) {
            holder.mDataLinear.setVisibility(View.GONE);
            holder.mProgressLinear.setVisibility(View.VISIBLE);

        } else {
            holder.itemView.setTag(mItems.get(position).mId);
            holder.mDataLinear.setVisibility(View.VISIBLE);
            holder.mProgressLinear.setVisibility(View.GONE);
            holder.mNameText.setText(mItems.get(position).mName);
            holder.mPriceText.setText(String.valueOf(mItems.get(position).mPrice));
            holder.mNumberText.setText(String.valueOf(mItems.get(position).mNumber));
        }
    }

    @Override
    public GoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.i_list, parent, false);
        view.setOnClickListener(this);
        return new GoodsViewHolder(view);
    }

    public void loadNext() {
        if (mItems.size() == scale) {
            //если последний элемент не является progress bar - загружено все
            //если нет - загружает последний
            if (mItems.get(scale - 1) != null) {
                return;
            }
        }
        final Item[] itemArray = DatabaseService.getInstance(mContext).getGoods(mItems.size(), PAGE_SIZE);
        for (int i = 0; i < PAGE_SIZE; i++) {
            if (itemArray[i] != null) {
                //если достигли края
                if (mItems.size() == scale) {
                    remove(mItems.size() - 1);
                    add(mItems.size(), itemArray[i]);
                } else {
                    add(mItems.size() - 1, itemArray[i]);
                }
            } else {
                break;
            }
        }
    }

    //либо добавлени нового
    public void addItem(final Item pItem) {
        //если длина списка == количество записей в таблице
        //и если последний элемент - крутилочка
        //показываем новый
        //иначе - нет, его и не должно пока быть
        if ((mItems.size() == scale) && (mItems.get(mItems.size() - 1) != null)) {
            add(mItems.size(), pItem);
        }
        scale++;
    }

    // замена существующего
    public void changeItem(final Item pItem) {
        int position = findPositionById(pItem.mId);
        remove(position);
        add(position, pItem);
    }

    private void deleteItem(int pPosition) {
        remove(pPosition);
        scale--;
    }

    private int findPositionById(final long pId) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).mId == pId) {
                return i;
            }
        }
        return -1;
    }

    private void add(final int position, final Item pItem) {
        mItems.add(position, pItem);
        notifyItemInserted(position);
    }

    private void remove(final int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onClick(View v) {
        long id = (long) v.getTag();
        showDialog(id);
    }

    @Override
    public boolean onLongClick(View v) {
        long id = (long) v.getTag();
        showDialog(id);
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final int position = findPositionById(mIdForDialog);
        switch (which) {
            case EDIT:
                mFragment.changeItem(mItems.get(position));
                break;
            case DELETE:
                mFragment.deleteItem(mItems.get(position));
                deleteItem(position);
                break;
        }
    }

    class GoodsViewHolder extends RecyclerView.ViewHolder {

        public final TextView mNameText;
        public final TextView mPriceText;
        public final TextView mNumberText;

        public final LinearLayout mProgressLinear;
        public final LinearLayout mDataLinear;

        public GoodsViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            mNameText = (TextView) itemView.findViewById(R.id.text_name);
            mPriceText = (TextView) itemView.findViewById(R.id.text_price);
            mNumberText = (TextView) itemView.findViewById(R.id.text_number);
            mProgressLinear = (LinearLayout) itemView.findViewById(R.id.linear_load);
            mDataLinear = (LinearLayout) itemView.findViewById(R.id.linear_data);
        }
    }
}