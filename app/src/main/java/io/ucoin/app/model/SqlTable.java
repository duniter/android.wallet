package io.ucoin.app.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public interface SqlTable {
    Integer count();

    int delete();

    Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder);

    Uri insert(ContentValues values);

    ;
}
