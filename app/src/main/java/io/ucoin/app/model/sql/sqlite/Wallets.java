package io.ucoin.app.model.sql.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.UcoinUris;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.UcoinWallets;
import io.ucoin.app.sqlite.SQLiteTable;

final public class Wallets extends Table
        implements UcoinWallets {

    private Long mCurrencyId;

    public Wallets(Context context, Long currencyId) {
        this(context, currencyId, SQLiteTable.Wallet.CURRENCY_ID + "=?", new String[]{currencyId.toString()});
    }

    private Wallets(Context context, Long currencyId, String selection, String[] selectionArgs) {
        this(context, currencyId, selection, selectionArgs, null);
    }

    private Wallets(Context context, Long currencyId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, UcoinUris.WALLET_URI, selection, selectionArgs, sortOrder);
        mCurrencyId = currencyId;
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

        Uri uri = insert(values);
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
        if (cursor != null) {
            ArrayList<UcoinWallet> data = new ArrayList<>();
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                data.add(new Wallet(mContext, id));
            }
            cursor.close();

            return data.iterator();
        }
        return null;
    }
}