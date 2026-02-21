package com.project.chosim.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.project.chosim.R;

@IgnoreExtraProperties
public class ShopItem implements Parcelable {

    private int id = 0;

    @Exclude
    private String title = "";

    @Exclude
    private int point = 0;

    @Exclude
    private int drawableResId = 0;


    public ShopItem(int id) {
        this.id = id;

        if (id == 0) {
            title = "하늘";
            point = 10;
            drawableResId = R.drawable.wallpaper_sky;

        } else if (id == 1) {
            title = "꽃";
            point = 10;
            drawableResId = R.drawable.wallpaper_flower;

        } else {
            title = "-";
            drawableResId = -1;
        }
    }

    protected ShopItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        point = in.readInt();
        drawableResId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeInt(point);
        dest.writeInt(drawableResId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShopItem> CREATOR = new Creator<ShopItem>() {
        @Override
        public ShopItem createFromParcel(Parcel in) {
            return new ShopItem(in);
        }

        @Override
        public ShopItem[] newArray(int size) {
            return new ShopItem[size];
        }
    };

    public int getId() {
        return id;
    }

    @Exclude
    public String getTitle() {
        return title;
    }

    @Exclude
    public int getPoint() {
        return point;
    }

    @Exclude
    public int getDrawableResId() {
        return drawableResId;
    }
}
