package io.ucoin.app.model.sql.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.DbProvider;
import io.ucoin.app.model.UcoinPeer;
import io.ucoin.app.model.UcoinPeers;
import io.ucoin.app.model.http_api.NetworkPeering;
import io.ucoin.app.sqlite.SQLiteTable;

final public class Peers extends Table
        implements UcoinPeers {

    private Long mCurrencyId;

    public Peers(Context context, Long currencyId) {
        this(context, currencyId, SQLiteTable.Peer.CURRENCY_ID + "=?", new String[]{currencyId.toString()});
    }

    private Peers(Context context, Long currencyId, String selection, String[] selectionArgs) {
        this(context, currencyId, selection, selectionArgs, null);
    }

    private Peers(Context context, Long currencyId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, DbProvider.PEER_URI, selection, selectionArgs, sortOrder);
        mCurrencyId = currencyId;
    }

    @Override
    public UcoinPeer add(NetworkPeering networkPeering) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Peer.CURRENCY_ID, mCurrencyId);
        values.put(SQLiteTable.Peer.PUBLIC_KEY, networkPeering.pubkey);
        values.put(SQLiteTable.Peer.SIGNATURE, networkPeering.signature);

        //todo use sql transaction and rollback in case of failure
        Uri uri = insert(values);
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
    public UcoinPeer at(int position) {
        Iterator<UcoinPeer> it = iterator();
        if(it == null) return null;

        while(position-- > 0) it.next();

        return it.next();
    }

    @Override
    public Iterator<UcoinPeer> iterator() {
        Cursor cursor = fetch();
        if(cursor != null) {
            ArrayList<UcoinPeer> data = new ArrayList<>();
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                data.add(new Peer(mContext, id));
            }
            cursor.close();

            return data.iterator();
        }
        return null;
    }
}