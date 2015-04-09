package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import io.ucoin.app.model.Entity;

public class SQLiteEntity implements Entity {

    protected Long mId;
    protected Context mContext;
    private Uri mUri;
    protected boolean mIsCached;

    public SQLiteEntity() {
        this(null, null, null);
    }

    public SQLiteEntity(Context context, Uri uri, Long id) {
        this(context, uri, id, false);
    }

    public SQLiteEntity(Context context, Uri uri, Long id, boolean isCached) {
        mContext = context;
        mUri = uri;
        mId = id;
        mIsCached = isCached;
    }

    public Long id() {
        return mId;
    }

    @Override
    public int delete() {
        Uri uri = Uri.withAppendedPath(mUri, mId.toString());
        return mContext.getContentResolver().delete(uri, null, null);
    }

    public Cursor fetch() {
        Cursor c = mContext.getContentResolver().query(
                mUri,
                null,
                BaseColumns._ID,
                new String[]{mId.toString()},
                BaseColumns._ID + " ASC LIMIT 1");

        return c;
    }

    /**
     * * getters ***
     */
    public String getString(String field) {
        Cursor cursor = getField(field);
        if (cursor == null) {
            return null;
        }
        String result = cursor.getString(cursor.getColumnIndex(field));
        cursor.close();
        return result;
    }


    public Long getLong(String field) {
        Cursor cursor = getField(field);
        if (cursor == null) {
            return null;
        }

        Long result = cursor.getLong(cursor.getColumnIndex(field));
        cursor.close();
        return result;
    }

    public Integer getInt(String field) {
        Cursor cursor = getField(field);
        if (cursor == null) {
            return null;
        }
        Integer result = cursor.getInt(cursor.getColumnIndex(field));
        cursor.close();
        return result;
    }

    public Float getFloat(String field) {
        Cursor cursor = getField(field);
        if (cursor == null) {
            return null;
        }
        Float result = cursor.getFloat(cursor.getColumnIndex(field));
        cursor.close();
        return result;
    }

    public Boolean getBoolean(String field) {
        Cursor cursor = getField(field);
        if (cursor == null) {
            return null;
        }
        String result = cursor.getString(cursor.getColumnIndex(field));
        cursor.close();
        return Boolean.valueOf(result);
    }

    /**
     * * Setters ***
     */
    public int setLong(String field, Long value) {
        Uri uri = Uri.withAppendedPath(mUri, mId.toString());
        ContentValues values = new ContentValues();
        values.put(field, value);
        return mContext.getContentResolver().update(uri, values, null, null);
    }

    public int setBoolean(String field, Boolean value) {
        Uri uri = Uri.withAppendedPath(mUri, mId.toString());
        ContentValues values = new ContentValues();
        values.put(field, value);
        return mContext.getContentResolver().update(uri, values, null, null);
    }


    /**
     * * INTERNAL methods ***
     */
    private Cursor getField(String field) {
        Uri uri = Uri.withAppendedPath(mUri, mId.toString());
        Cursor cursor = mContext.getContentResolver().query(uri, new String[]{field},
                null, null, null);

        if (!cursor.moveToNext())
            return null;

        return cursor;
    }

    protected SQLiteEntity(Parcel in) {
        mId = in.readByte() == 0x00 ? null : in.readLong();
        mContext = (Context) in.readValue(Context.class.getClassLoader());
        mUri = (Uri) in.readValue(Uri.class.getClassLoader());
        mIsCached = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mId);
        }
        dest.writeValue(mContext);
        dest.writeValue(mUri);
        dest.writeByte((byte) (mIsCached ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SQLiteEntity> CREATOR = new Parcelable.Creator<SQLiteEntity>() {
        @Override
        public SQLiteEntity createFromParcel(Parcel in) {
            return new SQLiteEntity(in);
        }

        @Override
        public SQLiteEntity[] newArray(int size) {
            return new SQLiteEntity[size];
        }
    };
}