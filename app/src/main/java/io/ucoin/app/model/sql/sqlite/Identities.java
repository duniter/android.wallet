package io.ucoin.app.model.sql.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.UcoinUris;
import io.ucoin.app.model.UcoinIdentities;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.technical.crypto.AddressFormatException;

final public class Identities extends Table
        implements UcoinIdentities {

    private Long mCurrencyId;

    public Identities(Context context, Long currencyId) {
        this(context, currencyId, SQLiteTable.Identity.CURRENCY_ID + "=?",new String[]{currencyId.toString()});
    }

    private Identities(Context context, Long currencyId, String selection, String[] selectionArgs) {
        this(context, currencyId, selection, selectionArgs, null);
    }

    private Identities(Context context, Long currencyId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, UcoinUris.IDENTITY_URI, selection, selectionArgs, sortOrder);
        mCurrencyId = currencyId;
    }

    @Override
    public UcoinIdentity getById(Long id) {
        return new Identity(mContext, id);
    }

    @Override
    public UcoinIdentity getIdentity() {
        Cursor c = fetch();
        if (c != null) {
            if (c.moveToNext()) {
                Long id = c.getLong(c.getColumnIndex(BaseColumns._ID));
                c.close();
                return new Identity(mContext, id);
            }
            c.close();
        }
        return null;
    }

    @Override
    public UcoinIdentity add(String uid, UcoinWallet wallet) throws AddressFormatException {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Identity.CURRENCY_ID, mCurrencyId);
        values.put(SQLiteTable.Identity.WALLET_ID, wallet.id());
        values.put(SQLiteTable.Identity.UID, uid);

        //insert identity
        Uri uri = insert(values);
        return new Identity(mContext, Long.parseLong(uri.getLastPathSegment()));
    }

    @Override
    public Iterator<UcoinIdentity> iterator() {
        Cursor cursor = fetch();
        if (cursor != null) {
            ArrayList<UcoinIdentity> data = new ArrayList<>();
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                data.add(new Identity(mContext, id));
            }
            cursor.close();

            return data.iterator();
        }
        return null;
    }
}