package ru.bolobanov.shop_manager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.bolobanov.shop_manager.Constants;
import ru.bolobanov.shop_manager.R;

/**
 * Created by Bolobanov Nikolay on 11.11.15.
 */
public class ShopDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = new String("goods.db");
    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    //create скрипт
    private static final String DATABASE_CREATE_SCRIPT = new StringBuilder().append("create table ").
            append(Constants.GOODS_TABLE).append(new String(" (")).append(BaseColumns._ID).
            append(new String(" INTEGER PRIMARY KEY AUTOINCREMENT, ")).
            append(Constants.COLUMN_NAME).append(new String(" TEXT NOT NULL, ")).
            append(Constants.COLUMN_PRICE).append(new String(" INTEGER, ")).
            append(Constants.COLUMN_NUMBER).append(new String(" INTEGER);")).toString();

    public ShopDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(DATABASE_CREATE_SCRIPT);
        insert(db);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * заполняем БД записями из raw/insert.sql
     *
     * @param db
     */
    private void insert(SQLiteDatabase db) {
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.insert);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = reader.readLine();
            while (line != null) {
                db.execSQL(line);
                line = reader.readLine();
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
