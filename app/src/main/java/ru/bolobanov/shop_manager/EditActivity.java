package ru.bolobanov.shop_manager;

import android.app.Activity;

import org.androidannotations.annotations.EActivity;

/**
 * Created by Bolobanov Nikolay on 12.11.15.
 */

@EActivity(R.layout.a_edit)
public class EditActivity extends Activity implements OnItemSaveListener {

    @Override
    public void saveItem(Item pItem, boolean isNewItem) {

    }
}
