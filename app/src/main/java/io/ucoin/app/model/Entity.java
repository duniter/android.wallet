package io.ucoin.app.model;

import android.os.Parcelable;

import java.io.Serializable;

public interface Entity extends Serializable, Parcelable{
    Long id();
    int delete();
}
