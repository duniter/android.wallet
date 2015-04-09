package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinTxIssuer;
import io.ucoin.app.model.UcoinTxIssuers;

final public class TxIssuers extends SQLiteEntities
        implements UcoinTxIssuers {

    @SuppressWarnings("unused")
    public static final Creator<TxIssuers> CREATOR = new Creator<TxIssuers>() {
        @Override
        public TxIssuers createFromParcel(Parcel in) {
            return new TxIssuers(in);
        }

        @Override
        public TxIssuers[] newArray(int size) {
            return new TxIssuers[size];
        }
    };
    private Long mTxId;

    public TxIssuers(Context context, Long txId) {
        this(context,
                txId,
                SQLiteTable.TxIssuer.TX_ID + "=?",
                new String[]{txId.toString()},
                SQLiteTable.TxIssuer.ISSUER_ORDER + " ASC");
    }

    private TxIssuers(Context context, Long txId, String selection, String[] selectionArgs) {
        this(context, txId, selection, selectionArgs, null);
    }

    private TxIssuers(Context context, Long txId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.TX_ISSUER_URI, selection, selectionArgs, sortOrder);
        mTxId = txId;
    }

    protected TxIssuers(Parcel in) {
        mTxId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinTxIssuer add(String publicKey, Integer issuerOrder) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.TxIssuer.TX_ID, mTxId);
        values.put(SQLiteTable.TxIssuer.PUBLIC_KEY, publicKey);
        values.put(SQLiteTable.TxIssuer.ISSUER_ORDER, issuerOrder);

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        if (Long.parseLong(uri.getLastPathSegment()) > 0) {
            return new TxIssuer(mContext, Long.parseLong(uri.getLastPathSegment()));
        } else {
            return null;
        }
    }

    @Override
    public UcoinTxIssuer getById(Long id) {
        return new TxIssuer(mContext, id);
    }

    @Override
    public Iterator<UcoinTxIssuer> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinTxIssuer> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new TxIssuer(mContext, id));
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
        if (mTxId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mTxId);
        }
    }
}