package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinPendingEndpoint;
import io.ucoin.app.model.UcoinPendingEndpoints;

final public class PendingEndpoints extends SQLiteEntities
        implements UcoinPendingEndpoints {

    @SuppressWarnings("unused")
    public static final Creator<PendingEndpoints> CREATOR = new Creator<PendingEndpoints>() {
        @Override
        public PendingEndpoints createFromParcel(Parcel in) {
            return new PendingEndpoints(in);
        }

        @Override
        public PendingEndpoints[] newArray(int size) {
            return new PendingEndpoints[size];
        }
    };

    public PendingEndpoints(Context context) {
        this(context, null, null, null);
    }

    private PendingEndpoints(Context context, String selection, String[] selectionArgs) {
        this(context, selection, selectionArgs, null);
    }

    private PendingEndpoints(Context context, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.ENDPOINT_PENDING_URI, selection, selectionArgs, sortOrder);
    }

    protected PendingEndpoints(Parcel in) {
    }

    @Override
    public UcoinPendingEndpoint add(String address, Integer port) {

        ContentValues values = new ContentValues();
        values.put(SQLiteTable.PendingEndpoint.ADDRESS, address);
        values.put(SQLiteTable.PendingEndpoint.PORT, port);

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        return new PendingEndpoint(mContext, Long.parseLong(uri.getLastPathSegment()));
    }

    @Override
    public Iterator<UcoinPendingEndpoint> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinPendingEndpoint> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new PendingEndpoint(mContext, id));
        }
        cursor.close();

        return data.iterator();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}