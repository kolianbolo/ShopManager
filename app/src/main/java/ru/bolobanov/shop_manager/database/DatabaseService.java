package ru.bolobanov.shop_manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import ru.bolobanov.shop_manager.Constants;
import ru.bolobanov.shop_manager.Item;

/**
 * Created by Bolobanov Nikolay on 13.11.15.
 */
public class DatabaseService {

    private static DatabaseService mInstance;

    private final Context mContext;
    private final ShopDatabaseHelper mDataseHelper;

    public static DatabaseService getInstance(Context pContext) {
        if (mInstance == null) {
            if (pContext != null) {
                mInstance = new DatabaseService(pContext.getApplicationContext());
            }
        }
        return mInstance;
    }

    private DatabaseService(Context pContext) {
        mContext = pContext;
        mDataseHelper = new ShopDatabaseHelper(mContext);
    }

    public int getGoodsSize() {
        final SQLiteDatabase sqLiteDatabase = mDataseHelper.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery(new StringBuilder().append("SELECT COUNT(_id) FROM ").
                append(Constants.GOODS_TABLE).append(" LIMIT 1").toString(), null);
        cursor.moveToFirst();
        final int size = cursor.getInt(0);
        cursor.close();
        return size;

    }

    public Item[] getGoods(long start_id, int count) {
        final Item[] returnedArray = new Item[count];
        final SQLiteDatabase database = mDataseHelper.getReadableDatabase();
        final Cursor cursor = database.query(Constants.GOODS_TABLE,
                new String[]{BaseColumns._ID, Constants.COLUMN_NAME, Constants.COLUMN_PRICE, Constants.COLUMN_NUMBER},
                "_id > ?", new String[]{String.valueOf(start_id)}, null, null, null, String.valueOf(count));
        cursor.moveToFirst();
        int i = 0;
        do {
            returnedArray[i] = new Item(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)),
                    cursor.getString(cursor.getColumnIndex(Constants.COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_PRICE)),
                    cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_NUMBER)));
            i++;
        } while (cursor.moveToNext());
        cursor.close();
        return returnedArray;
    }

    public Item saveGood(final Item pItem) {
        final SQLiteDatabase database = mDataseHelper.getWritableDatabase();
        database.beginTransaction();
        final ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_NAME, pItem.mName);
        values.put(Constants.COLUMN_PRICE, pItem.mPrice);
        values.put(Constants.COLUMN_NUMBER, pItem.mNumber);
        long id;
        final Item returned;
        if (pItem.mId == 0) {
            id = database.insert(Constants.GOODS_TABLE, new StringBuilder().append(Constants.COLUMN_NAME).
                    append(", ").append(Constants.COLUMN_PRICE).append(", ").append(Constants.COLUMN_NUMBER).toString(), values);
            returned = new Item(id, pItem.mName, pItem.mPrice, pItem.mNumber);
        } else {
            database.update(Constants.GOODS_TABLE, values, new StringBuilder().append(BaseColumns._ID).append(" = ?").toString(), new String[]{String.valueOf(pItem.mId)});
            returned = pItem;
        }
        database.setTransactionSuccessful();
        database.endTransaction();

        return returned;
    }

    public int deleteGood(final Item pItem) {
        final SQLiteDatabase database = mDataseHelper.getWritableDatabase();
        database.beginTransaction();
        final int deleted = database.delete(Constants.GOODS_TABLE, new StringBuilder().append(BaseColumns._ID).append(" = ?").toString(),
                new String[]{Long.toString(pItem.mId)});
        if (deleted == 1) {
            database.setTransactionSuccessful();
        }
        database.endTransaction();
        return deleted;
    }
}
