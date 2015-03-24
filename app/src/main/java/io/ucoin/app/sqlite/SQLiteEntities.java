package io.ucoin.app.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import io.ucoin.app.model.Entities;

public abstract class SQLiteEntities implements Entities {

    protected Context mContext;
    protected Uri mUri;

    protected SQLiteEntities() {
        mContext = null;
        mUri = null;
    }

    protected SQLiteEntities(Context context, Uri uri) {
        mContext = context;
        mUri = uri;
    }

    public int delete(Long id) {
        return mContext.getContentResolver().delete(Uri.withAppendedPath(mUri, id.toString()),
                null, null);
    }

    @Override
    public int count() {
        Cursor c = mContext.getContentResolver().query(
                mUri,
                null,
                null,
                null,
                null);
        int count = c.getCount();
        c.close();
        return count;
    }


    public Long queryUnique(String selection, String[] selectionArgs) {
        Cursor c = mContext.getContentResolver().query(
                mUri,
                new String[]{BaseColumns._ID},
                selection,
                selectionArgs,
                BaseColumns._ID +" ASC LIMIT 1");
        if (c.moveToNext()) {
            Long id = c.getLong(c.getColumnIndex(BaseColumns._ID));
            c.close();
            return id;
        } else {
            c.close();
            return null;
        }
    }
}
