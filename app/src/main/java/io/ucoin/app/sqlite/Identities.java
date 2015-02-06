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
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.UcoinIdentities;

final public class Identities extends SQLiteEntities
        implements UcoinIdentities, Parcelable {

    private Long mCurrencyId;
    private ArrayList<UcoinIdentity> mIdentitiesArray;

    public Identities(Context context, Long currencyId) {
        super(context, Provider.IDENTITY_URI);
        mCurrencyId = currencyId;
    }

    public Identities(Long currencyId, ArrayList<UcoinIdentity> identities) {
        mCurrencyId = currencyId;
        if (identities == null) {
            mIdentitiesArray = new ArrayList<>();
        } else {
            mIdentitiesArray = identities;
        }
    }

    @Override
    public UcoinIdentity newIdentity(Long walletId, String uid) {
        return new Identity(mCurrencyId, walletId, uid);
    }

    @Override
    public UcoinIdentity add(UcoinIdentity identity) {
        ContentValues values = new ContentValues();
        values.put(Contract.Identity.CURRENCY_ID, identity.currencyId());
        values.put(Contract.Identity.WALLET_ID, identity.walletId());
        values.put(Contract.Identity.UID, identity.uid());
        values.put(Contract.Identity.SELF, identity.self());
        values.put(Contract.Identity.TIMESTAMP, identity.timestamp());

        //insert identity
        Uri uri = mContext.getContentResolver().insert(mUri, values);
        return getById(Long.parseLong(uri.getLastPathSegment()));
    }

    @Override
    public UcoinIdentity getById(Long id) {
        return new Identity(mContext, id);
    }

    @Override
    public Iterator<UcoinIdentity> iterator() {
        if (mContext == null) {
            return mIdentitiesArray.iterator();
        }

        final Cursor identityCursor = mContext.getContentResolver().query(mUri, null,
                null, null, null);

        return new Iterator<UcoinIdentity>() {
            @Override
            public boolean hasNext() {
                if (identityCursor.moveToNext())
                    return true;
                else {
                    identityCursor.close();
                    return false;
                }
            }

            @Override
            public UcoinIdentity next() {
                Long id = identityCursor.getLong(identityCursor.getColumnIndex(Contract.Identity._ID));
                return new Identity(mContext, id);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected Identities(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
        if (in.readByte() == 0x01) {
            mIdentitiesArray = new ArrayList<>();
            in.readList(mIdentitiesArray, UcoinIdentity.class.getClassLoader());
        } else {
            mIdentitiesArray = null;
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
        if (mIdentitiesArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mIdentitiesArray);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Identities> CREATOR = new Parcelable.Creator<Identities>() {
        @Override
        public Identities createFromParcel(Parcel in) {
            return new Identities(in);
        }

        @Override
        public Identities[] newArray(int size) {
            return new Identities[size];
        }
    };
}