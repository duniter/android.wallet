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
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.UcoinWallets;

final public class Wallets extends SQLiteEntities
        implements UcoinWallets {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Wallets> CREATOR = new Parcelable.Creator<Wallets>() {
        @Override
        public Wallets createFromParcel(Parcel in) {
            return new Wallets(in);
        }

        @Override
        public Wallets[] newArray(int size) {
            return new Wallets[size];
        }
    };
    private Long mCurrencyId;
    private ArrayList<UcoinWallet> mWalletsArray;

    public Wallets(Context context, Long currencyId) {
        this(context, currencyId, SQLiteTable.Wallet.CURRENCY_ID + "=?", new String[]{currencyId.toString()});
    }

    private Wallets(Context context, Long currencyId, String selection, String[] selectionArgs) {
        this(context, currencyId, selection, selectionArgs, null);
    }

    private Wallets(Context context, Long currencyId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.WALLET_URI, selection, selectionArgs, sortOrder);
        mCurrencyId = currencyId;
    }

    protected Wallets(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
        if (in.readByte() == 0x01) {
            mWalletsArray = new ArrayList<>();
            in.readList(mWalletsArray, UcoinWallet.class.getClassLoader());
        } else {
            mWalletsArray = null;
        }
    }

    @Override
    public UcoinWallet add(String salt, String alias, String publicKey) {
        return add(salt, alias, publicKey, null);
    }

    @Override
    public UcoinWallet add(String salt, String alias, String publicKey, String privateKey) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Wallet.CURRENCY_ID, mCurrencyId);
        values.put(SQLiteTable.Wallet.SALT, salt);
        values.put(SQLiteTable.Wallet.ALIAS, alias);
        values.put(SQLiteTable.Wallet.PUBLIC_KEY, publicKey);
        values.put(SQLiteTable.Wallet.PRIVATE_KEY, privateKey);

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        if (Long.parseLong(uri.getLastPathSegment()) > 0) {
            return new Wallet(mContext, Long.parseLong(uri.getLastPathSegment()));
        } else {
            return null;
        }
    }

    @Override
    public UcoinWallet getById(Long id) {
        return new Wallet(mContext, id);
    }

    @Override
    public UcoinWallet getByPublicKey(String publicKey) {
        String selection = SQLiteTable.Wallet.CURRENCY_ID + "=? AND " +
                SQLiteTable.Wallet.PUBLIC_KEY + "=?";
        String[] selectionArgs = new String[]{
                mCurrencyId.toString(),
                publicKey};
        UcoinWallets wallets = new Wallets(mContext, mCurrencyId, selection, selectionArgs, null);
        if (wallets.iterator().hasNext()) {
            return wallets.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public Iterator<UcoinWallet> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinWallet> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new Wallet(mContext, id));
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
        if (mWalletsArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mWalletsArray);
        }
    }
}