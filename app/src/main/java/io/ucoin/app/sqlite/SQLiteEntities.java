package io.ucoin.app.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import io.ucoin.app.model.Entities;

public abstract class SQLiteEntities implements Entities {

    protected Context mContext;
    protected Uri mUri;
    protected String mSelection;
    protected String[] mSelectionArgs;
    protected String mSortOrder;

    protected SQLiteEntities() {
        this(null, null, null, null, null);
    }

    protected SQLiteEntities(Context context, Uri uri) {
        this(context, uri, null, null, null);
    }

    protected SQLiteEntities(Context context, Uri uri, String selection, String[] selectionArgs) {
        this(context, uri, selection, selectionArgs, null);
    }

    protected SQLiteEntities(Context context, Uri uri, String selection, String[] selectionArgs, String sortOrder) {
        mContext = context;
        mUri = uri;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
    }

    public int delete(Long id) {
        return mContext.getContentResolver().delete(Uri.withAppendedPath(mUri, id.toString()),
                null, null);
    }

    @Override
    public Integer count() {
        Cursor c = mContext.getContentResolver().query(
                mUri,
                null,
                mSelection,
                mSelectionArgs,
                mSortOrder);
        int count = c.getCount();
        c.close();
        return count;
    }

    //todo concatenate selection and mSelection, same for selectionArgs
    @Override
    public Long queryUnique(String selection, String[] selectionArgs) {
        Cursor c = mContext.getContentResolver().query(
                mUri,
                new String[]{BaseColumns._ID},
                selection,
                selectionArgs,
                BaseColumns._ID + " ASC LIMIT 1");
        if (c.moveToNext()) {
            Long id = c.getLong(c.getColumnIndex(BaseColumns._ID));
            c.close();
            return id;
        } else {
            c.close();
            return null;
        }
    }

    @Override
    public Integer delete() {
        return mContext.getContentResolver().delete(mUri, mSelection, mSelectionArgs);
    }

    public Cursor fetch() {
        return mContext.getContentResolver().query(mUri, null,
                mSelection, mSelectionArgs, mSortOrder);
    }
}