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
import io.ucoin.app.model.UcoinEndpoints;

final public class Endpoints extends SQLiteEntities
        implements UcoinEndpoints {

    private Long mPeerId;
    private ArrayList<UcoinEndpoint> mEndpointsArray;

    public Endpoints(Context context, Long peerId) {
        super(context, Provider.ENDPOINT_URI);
        mPeerId = peerId;
    }

    public Endpoints(Long peerId, io.ucoin.app.model.http_api.Peer peer) {
        mPeerId = peerId;
        mEndpointsArray = new ArrayList<>();
        for(String endpointStr : peer.endpoints) {
            mEndpointsArray.add(new Endpoint(mPeerId, endpointStr));
        }
    }

    public Endpoints(Long peerId, ArrayList<UcoinEndpoint> endpoints) {
        mPeerId = peerId;
        if (endpoints == null) {
            mEndpointsArray = new ArrayList<>();
        } else {
            mEndpointsArray = endpoints;
        }
    }

    @Override
    public UcoinEndpoint newEndpoint(String endpointStr) {
        return new Endpoint(mPeerId, endpointStr);
    }

    @Override
    public UcoinEndpoint add(UcoinEndpoint endpoint) {
        if (mContext != null) {
            ContentValues values = new ContentValues();
            values.put(Contract.Endpoint.PEER_ID, endpoint.peerId());
            values.put(Contract.Endpoint.URL, endpoint.url());
            values.put(Contract.Endpoint.IPV4, endpoint.ipv4());
            values.put(Contract.Endpoint.IPV6, endpoint.ipv6());
            values.put(Contract.Endpoint.PORT, endpoint.port());

            Uri uri = mContext.getContentResolver().insert(mUri, values);
            return getById(Long.parseLong(uri.getLastPathSegment()));
        } else {
            mEndpointsArray.add(endpoint);
            return endpoint;
        }
    }

    @Override
    public UcoinEndpoint getById(Long id) {
        return new Endpoint(mContext, id);
    }

    @Override
    public Iterator<UcoinEndpoint> iterator() {
        if (mContext == null) {
            return mEndpointsArray.iterator();
        }
        String selection = Contract.Endpoint.PEER_ID + "=?";
        String[] selectionArgs = new String[]{mPeerId.toString()};
        final Cursor endpointsCursor = mContext.getContentResolver().query(mUri, null,
                selection, selectionArgs, null);

        return new Iterator<UcoinEndpoint>() {
            @Override
            public boolean hasNext() {
                if (endpointsCursor.moveToNext())
                    return true;
                else {
                    endpointsCursor.close();
                    return false;
                }
            }

            @Override
            public UcoinEndpoint next() {
                Long id = endpointsCursor.getLong(endpointsCursor.getColumnIndex(Contract.Endpoint._ID));
                return new Endpoint(mContext, id);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected Endpoints(Parcel in) {
        mPeerId = in.readByte() == 0x00 ? null : in.readLong();
        if (in.readByte() == 0x01) {
            mEndpointsArray = new ArrayList<>();
            in.readList(mEndpointsArray, UcoinEndpoint.class.getClassLoader());
        } else {
            mEndpointsArray = null;
        }
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
        if (mEndpointsArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mEndpointsArray);
        }
    }

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
}