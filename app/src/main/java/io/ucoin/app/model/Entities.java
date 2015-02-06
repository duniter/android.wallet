package io.ucoin.app.model;

import android.os.Parcelable;

import java.io.Serializable;

public interface Entities extends Serializable, Parcelable {
    public int count();
    public Long queryUnique(String selection, String[] selectionArgs);
}
