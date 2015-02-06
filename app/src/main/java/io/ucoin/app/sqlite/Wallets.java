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
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.UcoinWallets;

final public class Wallets extends SQLiteEntities
        implements UcoinWallets {

    private Long mCurrencyId;
    private ArrayList<UcoinWallet> mWalletsArray;

    public Wallets(Context context, Long currencyId) {
        super(context, Provider.WALLET_URI);
        mCurrencyId = currencyId;
    }

    public Wallets(Long currencyId, ArrayList<UcoinWallet> wallets) {
        mCurrencyId = currencyId;
        if (wallets == null) {
            mWalletsArray = new ArrayList<>();
        } else {
            mWalletsArray = wallets;
        }
    }

    @Override
    public UcoinWallet newWallet(String salt, String publicKey, String alias) {
        return new Wallet(mCurrencyId, salt, publicKey, alias);
    }

    public UcoinWallet newWallet(String salt, String publicKey, String privateKey,
                                 String alias) {
        return new Wallet(mCurrencyId, salt, publicKey, privateKey, alias);
    }

    @Override
    public UcoinWallet add(UcoinWallet wallet) {
        if (mContext != null) {
            ContentValues values = new ContentValues();
            values.put(Contract.Wallet.CURRENCY_ID, wallet.currencyId());
            values.put(Contract.Wallet.SALT, wallet.salt());
            values.put(Contract.Wallet.PUBLIC_KEY, wallet.publicKey());
            values.put(Contract.Wallet.PRIVATE_KEY, wallet.privateKey());
            values.put(Contract.Wallet.ALIAS, wallet.alias());

            Uri uri = mContext.getContentResolver().insert(mUri, values);
            return getById(Long.parseLong(uri.getLastPathSegment()));
        }else {
            mWalletsArray.add(wallet);
            return wallet;
        }
    }

    @Override
    public UcoinWallet getById(Long id) {
        return new Wallet(mContext, id);
    }

    @Override
    public Iterator<UcoinWallet> iterator() {
        if (mContext == null) {
            return mWalletsArray.iterator();
        }
        String selection = Contract.Wallet.CURRENCY_ID + "=?";
        String[] selectionArgs = new String[]{mCurrencyId.toString()};
        final Cursor walletsCursor = mContext.getContentResolver().query(mUri, null,
                selection, selectionArgs, null);

        return new Iterator<UcoinWallet>() {
            @Override
            public boolean hasNext() {
                if (walletsCursor.moveToNext())
                    return true;
                else {
                    walletsCursor.close();
                    return false;
                }
            }

            @Override
            public UcoinWallet next() {
                Long id = walletsCursor.getLong(walletsCursor.getColumnIndex(Contract.Wallet._ID));
                return new Wallet(mContext, id);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
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
}