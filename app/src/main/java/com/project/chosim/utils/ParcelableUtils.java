package com.project.chosim.utils;

import android.os.Parcel;
import android.os.Parcelable;


public class ParcelableUtils {

    public static <T extends Parcelable> T copy(T orig) {
        Parcel p = Parcel.obtain();
        orig.writeToParcel(p, 0);
        p.setDataPosition(0);
        T copy = null;
        try {
            copy = (T) orig.getClass().getDeclaredConstructor(new Class[]{Parcel.class}).newInstance(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return copy;
    }
}
