package io.ucoin.app.model.sql.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.DbProvider;
import io.ucoin.app.model.UcoinContact;
import io.ucoin.app.model.UcoinContacts;
import io.ucoin.app.sqlite.SQLiteTable;

public class Contacts extends Table
        implements UcoinContacts {

    private Long mCurrencyId;

    public Contacts(Context context, Long currencyId) {
        this(context, currencyId, SQLiteTable.Contact.CURRENCY_ID + "=?", new String[]{currencyId.toString()});
    }

    private Contacts(Context context, Long currencyId, String selection, String[] selectionArgs) {
        this(context, currencyId, selection, selectionArgs, null);
    }

    private Contacts(Context context, Long currencyId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, DbProvider.CONTACT_URI, selection, selectionArgs, sortOrder);
        mCurrencyId = currencyId;
    }

    @Override
    public UcoinContact add(String name, String publicKey) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Contact.CURRENCY_ID, mCurrencyId);;
        values.put(SQLiteTable.Contact.NAME, name);;
        values.put(SQLiteTable.Contact.PUBLIC_KEY, publicKey);;

        Uri uri = insert(values);
        return new Contact(mContext, Long.parseLong(uri.getLastPathSegment()));
    }

    @Override
    public UcoinContact getById(Long id) {
        return new Contact(mContext, id);
    }

    @Override
    public UcoinContact getByName(String name) {
        String selection = SQLiteTable.Contact.CURRENCY_ID + "=? AND " + SQLiteTable.Contact.NAME + " LIKE ?";
        String[] selectionArgs = new String[]{mCurrencyId.toString(), name};
        UcoinContacts contacts = new Contacts(mContext, mCurrencyId, selection, selectionArgs);
        if (contacts.iterator().hasNext()) {
            return contacts.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public UcoinContact getByPublicKey(String publicKey) {
        String selection = SQLiteTable.Contact.CURRENCY_ID + "=? AND " + SQLiteTable.Contact.PUBLIC_KEY + " LIKE ?";
        String[] selectionArgs = new String[]{mCurrencyId.toString(), publicKey};
        UcoinContacts contacts = new Contacts(mContext, mCurrencyId, selection, selectionArgs);
        if (contacts.iterator().hasNext()) {
            return contacts.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public Iterator<UcoinContact> iterator() {
        Cursor cursor = fetch();
        if (cursor != null) {
            ArrayList<UcoinContact> data = new ArrayList<>();
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                data.add(new Contact(mContext, id));
            }
            cursor.close();

            return data.iterator();
        }
        return null;
    }
}