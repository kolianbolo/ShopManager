package ru.bolobanov.shop_manager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.EActivity;

/**
 * Created by Bolobanov Nikolay on 12.11.15.
 */

@EActivity(R.layout.a_edit)
public class EditActivity extends AppCompatActivity implements OnItemSaveListener {

    @Override
    public void saveItem(Item pItem, boolean isNewItem) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.ITEM_KEY, pItem);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


}
