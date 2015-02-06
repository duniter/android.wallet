package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinEndpoint;
import io.ucoin.app.model.UcoinPeer;
import io.ucoin.app.model.UcoinPeers;

final public class Peers extends SQLiteEntities
        implements UcoinPeers {

    private Long mCurrencyId;
    private ArrayList<UcoinPeer> mPeersArray;

    public Peers(Context context, Long currencyId) {
        super(context, Provider.PEER_URI);
        mCurrencyId = currencyId;
    }

    public Peers(Long currencyId, ArrayList<UcoinPeer> peers) {
        mCurrencyId = currencyId;
        if (peers == null) {
            mPeersArray = new ArrayList<>();
        } else {
            mPeersArray = peers;
        }
    }

    public UcoinPeer newPeer(io.ucoin.app.model.http_api.Peer peer) {
        return new Peer(mCurrencyId, peer);
    }

    @Override
    public UcoinPeer add(UcoinPeer peer) {
        if (mContext != null) {
            ContentValues values = new ContentValues();
            values.put(Contract.Peer.CURRENCY_ID, peer.currencyId());
            values.put(Contract.Peer.PUBLIC_KEY, peer.publicKey());
            values.put(Contract.Peer.SIGNATURE, peer.signature());

            Uri uri = mContext.getContentResolver().insert(mUri, values);
            UcoinPeer sqlPeer = getById(Long.parseLong(uri.getLastPathSegment()));

            for (UcoinEndpoint endpoint : peer.endpoints()) {
                endpoint = new Endpoint(sqlPeer.id(), endpoint);
                sqlPeer.endpoints().add(endpoint);
            }

            return peer;
        } else {
            mPeersArray.add(peer);
            return peer;
        }
    }

    @Override
    public UcoinPeer getById(Long id) {
        return new Peer(mContext, id);
    }

    @Override
    public Iterator<UcoinPeer> iterator() {
        if (mContext == null) {
            return mPeersArray.iterator();
        }

        String selection = Contract.Peer.CURRENCY_ID + "=?";
        String[] selectionArgs = new String[]{mCurrencyId.toString()};
        final Cursor peersCursor = mContext.getContentResolver().query(mUri, null,
                selection, selectionArgs, null);

        return new Iterator<UcoinPeer>() {
            @Override
            public boolean hasNext() {
                if (peersCursor.moveToNext())
                    return true;
                else {
                    peersCursor.close();
                    return false;
                }
            }

            @Override
            public UcoinPeer next() {
                Long id = peersCursor.getLong(peersCursor.getColumnIndex(Contract.Peer._ID));
                return new Peer(mContext, id);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected Peers(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
        if (in.readByte() == 0x01) {
            mPeersArray = new ArrayList<UcoinPeer>();
            in.readList(mPeersArray, UcoinPeer.class.getClassLoader());
        } else {
            mPeersArray = null;
        }
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
        if (mPeersArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mPeersArray);
        }
    }

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
}