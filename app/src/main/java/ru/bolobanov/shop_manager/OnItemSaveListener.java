package ru.bolobanov.shop_manager;

/**
 * Created by Bolobanov Nikolay on 16.11.15.
 */
public interface OnItemSaveListener {
    void saveItem(final Item pItem, final boolean isNewItem);

}
