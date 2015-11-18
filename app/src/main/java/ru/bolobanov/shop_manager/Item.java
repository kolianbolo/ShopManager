package ru.bolobanov.shop_manager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bolobanov Nikolay on 13.11.15.
 */
public class Item implements Parcelable {
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

    // конструктор, считывающий данные из Parcel
    private Item(Parcel parcel) {
        mId = parcel.readLong();
        mName = parcel.readString();
        mPrice = parcel.readInt();
        mNumber = parcel.readInt();
    }

    public String s;
    public int i;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mId);
        parcel.writeString(mName);
        parcel.writeInt(mPrice);
        parcel.writeInt(mNumber);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        // распаковываем объект из Parcel
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String toString() {
        final String whitespace = new String(" ");
        return new StringBuilder().append(mId).append(whitespace).append(mName).append(whitespace).
                append(mPrice).append(whitespace).append(mNumber).append("\n").toString();
    }

}