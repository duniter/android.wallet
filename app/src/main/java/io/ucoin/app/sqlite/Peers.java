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
import io.ucoin.app.model.UcoinPeer;
import io.ucoin.app.model.UcoinPeers;
import io.ucoin.app.model.http_api.NetworkPeering;

final public class Peers extends SQLiteEntities
        implements UcoinPeers {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Peers> CREATOR = new Parcelable.Creator<Peers>() {
        @Override
        public Peers createFromParcel(Parcel in) {
            return new Peers(in);
        }

        @Override
        public Peers[] newArray(int size) {
            return new Peers[size];
        }
    };
    private Long mCurrencyId;

    public Peers(Context context, Long currencyId) {
        this(context, currencyId, SQLiteTable.Peer.CURRENCY_ID + "=?", new String[]{currencyId.toString()});
    }

    private Peers(Context context, Long currencyId, String selection, String[] selectionArgs) {
        this(context, currencyId, selection, selectionArgs, null);
    }

    private Peers(Context context, Long currencyId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.PEER_URI, selection, selectionArgs, sortOrder);
        mCurrencyId = currencyId;
    }

    protected Peers(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinPeer add(NetworkPeering networkPeering) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Peer.CURRENCY_ID, mCurrencyId);
        values.put(SQLiteTable.Peer.PUBLIC_KEY, networkPeering.pubkey);
        values.put(SQLiteTable.Peer.SIGNATURE, networkPeering.signature);

        //todo use sql transaction and rollaback in case of failure
        Uri uri = mContext.getContentResolver().insert(mUri, values);
        if (Long.parseLong(uri.getLastPathSegment()) > 0) {
            UcoinPeer peer = new Peer(mContext, Long.parseLong(uri.getLastPathSegment()));

            for (NetworkPeering.Endpoint endpoint : networkPeering.endpoints) {
                peer.endpoints().add(endpoint);
            }

            return peer;
        }
        return null;

    }

    @Override
    public UcoinPeer getById(Long id) {
        return new Peer(mContext, id);
    }

    @Override
    public Iterator<UcoinPeer> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinPeer> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new Peer(mContext, id));
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
        if (mCurrencyId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mCurrencyId);
        }
    }
}