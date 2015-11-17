package ru.bolobanov.shop_manager;

/**
 * Created by Bolobanov Nikolay on 16.11.15.
 */
public interface OnItemChangeListener {

    Item createItem();

    void editItem(final Item pItem);

    void deleteItem(final Item pItem);
}
