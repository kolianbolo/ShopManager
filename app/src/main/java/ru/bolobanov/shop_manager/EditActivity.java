package ru.bolobanov.shop_manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.EActivity;

/**
 * Created by Bolobanov Nikolay on 12.11.15.
 */

@EActivity(R.layout.a_edit)
public class EditActivity extends AppCompatActivity implements OnItemSaveListener {

    private final String HEADER_KEY = "header_key";

    @Override
    public void saveItem(Item pItem, boolean isNewItem) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.ITEM_KEY, pItem);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        if (savedState == null) {
            savedState = new Bundle();
        }
        savedState.putCharSequence(HEADER_KEY, getSupportActionBar().getTitle());
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedState) {
        if (savedState != null) {
            getSupportActionBar().setTitle(savedState.getCharSequence(HEADER_KEY));
        }
        super.onRestoreInstanceState(savedState);
    }
}
