package ru.bolobanov.shop_manager;

/**
 * Created by Bolobanov Nikolay on 13.11.15.
 */
public class Item {
    public final long mId;
    public final String mName;
    public final int mPrice;
    public final int mNumber;

    public Item(long pId, String pName, int pPrice, int pCount) {
        mId = pId;
        mName = pName;
        mPrice = pPrice;
        mNumber = pCount;
    }

    public String toString() {
        final String whitespace = new String(" ");
        return new StringBuilder().append(mId).append(whitespace).append(mName).append(whitespace).
                append(mPrice).append(whitespace).append(mNumber).append("\n").toString();
    }
}