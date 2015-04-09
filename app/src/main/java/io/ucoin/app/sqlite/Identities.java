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
import io.ucoin.app.model.UcoinIdentities;
import io.ucoin.app.model.UcoinIdentity;

final public class Identities extends SQLiteEntities
        implements UcoinIdentities, Parcelable {

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
    private Long mCurrencyId;

    public Identities(Context context, Long currencyId) {
        super(context, Provider.IDENTITY_URI);
        mCurrencyId = currencyId;
    }

    public Identities(Long currencyId, ArrayList<UcoinIdentity> identities) {
        mCurrencyId = currencyId;
    }

    protected Identities(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinIdentity getById(Long id) {
        return new Identity(mContext, id);
    }

    @Override
    public UcoinIdentity newIdentity(Long walletId, String uid) {
        return new Identity(mCurrencyId, walletId, uid);
    }

    @Override
    public UcoinIdentity add(UcoinIdentity identity) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Identity.CURRENCY_ID, identity.currencyId());
        values.put(SQLiteTable.Identity.WALLET_ID, identity.walletId());
        values.put(SQLiteTable.Identity.UID, identity.uid());
        values.put(SQLiteTable.Identity.SELF, identity.self());
        values.put(SQLiteTable.Identity.TIMESTAMP, identity.timestamp());

        //insert identity
        Uri uri = mContext.getContentResolver().insert(mUri, values);
        return new Identity(mContext, Long.parseLong(uri.getLastPathSegment()));
    }

    @Override
    public Iterator<UcoinIdentity> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinIdentity> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new Identity(mContext, id));
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