package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.Provider;
import io.ucoin.app.enums.EndpointProtocol;
import io.ucoin.app.model.UcoinEndpoint;
import io.ucoin.app.model.UcoinEndpoints;
import io.ucoin.app.model.http_api.NetworkPeering;

final public class Endpoints extends SQLiteEntities
        implements UcoinEndpoints {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Endpoints> CREATOR = new Parcelable.Creator<Endpoints>() {
        @Override
        public Endpoints createFromParcel(Parcel in) {
            return new Endpoints(in);
        }

        @Override
        public Endpoints[] newArray(int size) {
            return new Endpoints[size];
        }
    };
    private Long mPeerId;

    public Endpoints(Context context, Long peerId) {
        this(context, peerId, SQLiteTable.Endpoint.PEER_ID + "=?",new String[]{peerId.toString()});
    }

    private Endpoints(Context context, Long peerId, String selection, String[] selectionArgs) {
        this(context, peerId, selection, selectionArgs, null);
    }

    private Endpoints(Context context, Long peerId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.ENDPOINT_URI, selection, selectionArgs, sortOrder);
        mPeerId = peerId;
    }

    protected Endpoints(Parcel in) {
        mPeerId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinEndpoint add(NetworkPeering.Endpoint endpoint) {

        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Endpoint.PEER_ID, mPeerId);
        values.put(SQLiteTable.Endpoint.PROTOCOL, endpoint.protocol.name());
        values.put(SQLiteTable.Endpoint.URL, endpoint.url);
        values.put(SQLiteTable.Endpoint.IPV4, endpoint.ipv4);
        values.put(SQLiteTable.Endpoint.IPV6, endpoint.ipv6);
        values.put(SQLiteTable.Endpoint.PORT, endpoint.port);

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        return new Endpoint(mContext, Long.parseLong(uri.getLastPathSegment()));
    }

    @Override
    public UcoinEndpoint getById(Long id) {
        return new Endpoint(mContext, id);
    }

    @Override
    public Iterator<UcoinEndpoint> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinEndpoint> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new Endpoint(mContext, id));
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
        if (mPeerId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mPeerId);
        }
    }

    @Override
    public Endpoints getByProtocol(EndpointProtocol protocol) {
        String selection = SQLiteTable.Endpoint.PEER_ID + "=? AND " +
                SQLiteTable.Endpoint.PROTOCOL + "=?";
        String[] selectionArgs = new String[]{mPeerId.toString(), protocol.name()};
        return new Endpoints(mContext, mPeerId, selection, selectionArgs);
    }
}