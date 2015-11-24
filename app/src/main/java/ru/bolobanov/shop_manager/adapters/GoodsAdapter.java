package ru.bolobanov.shop_manager.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.bolobanov.shop_manager.Item;
import ru.bolobanov.shop_manager.R;
import ru.bolobanov.shop_manager.database.DatabaseService;
import ru.bolobanov.shop_manager.fragments.GoodsListFragment;

import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by Bolobanov Nikolay on 12.11.15.
 */

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> implements View.OnClickListener, View.OnLongClickListener, DialogInterface.OnClickListener {

    public final int START_SIZE = 20;
    public final int PAGE_SIZE = 10;

    public final int EDIT = 0;
    public final int DELETE = 1;

    private final ArrayList<Item> mItems;

    private final LayoutInflater mInflater;

    private final Context mContext;

    private final GoodsListFragment mFragment;

    private Dialog mDialog;

    private long mIdForDialog = -1;

    public int scale;

    public GoodsAdapter(final Context pContext, final GoodsListFragment pFragment) {
        mContext = pContext;
        mFragment = pFragment;
        mInflater = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scale = DatabaseService.getInstance(mContext).getGoodsSize();
        mItems = new ArrayList<>();
        final Item[] itemArray = DatabaseService.getInstance(mContext).getGoods(0, START_SIZE);
        for (int i = 0; i < START_SIZE; i++) {
            if (itemArray[i] != null) {
                mItems.add(itemArray[i]);
            }
        }
        if (START_SIZE < scale) {
            mItems.add(null);
        }
    }

    public GoodsAdapter(final Context pContext, final GoodsListFragment pFragment, final int pScale, final ArrayList<Item> pItems) {
        mContext = pContext;
        mFragment = pFragment;
        mInflater = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scale = pScale;
        mItems = pItems;
    }

    public ArrayList<Item> getItems() {
        return mItems;
    }


    public int getScale() {
        return scale;
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
            holder.itemView.setTag(null);
        } else {
            holder.itemView.setTag(mItems.get(position).mId);
            holder.mDataLinear.setVisibility(View.VISIBLE);
            holder.mProgressLinear.setVisibility(View.GONE);
            holder.mNameText.setText(mItems.get(position).mName);
            holder.mPriceText.setText(mItems.get(position).mPrice + "$");
            holder.mNumberText.setText(new StringBuilder().append(mItems.get(position).mNumber).
                    append(" ").append(mContext.getString(R.string.pcs)));
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
            if (mItems.get(scale - 1) != null) {
                return;
            }
        }
        final long extremeId = mItems.get(mItems.size() - 2).mId;
        final Item[] itemArray = DatabaseService.getInstance(mContext).getGoods(extremeId, PAGE_SIZE);
        for (int i = 0; i < PAGE_SIZE; i++) {
            if (itemArray[i] != null) {
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


    public void addItem(final Item pItem) {
        if ((mItems.size() == scale) && (mItems.get(mItems.size() - 1) != null)) {
            add(mItems.size(), pItem);
        }
        scale++;
    }

    public void changeItem(final Item pItem) {
        int position = findPositionById(pItem.mId);
        remove(position);
        add(position, pItem);
    }

    private void deleteItem(int pPosition) {
        remove(pPosition);
        scale--;
        if (mItems.size() - pPosition < START_SIZE) {
            loadNext();
        }
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
        final Object tag = v.getTag();
        if (tag != null) {
            long id = (long) tag;
            showDialog(id);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        final Object tag = v.getTag();
        if (tag != null) {
            long id = (long) tag;
            showDialog(id);
        }
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final int position = findPositionById(mIdForDialog);
        final Item clickedItem = mItems.get(position);
        switch (which) {
            case EDIT:
                mFragment.changeItem(clickedItem);
                break;
            case DELETE:
                mFragment.deleteItem(clickedItem);
                deleteItem(position);
                break;
        }

    }

    class GoodsViewHolder extends ViewHolder {

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