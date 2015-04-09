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
import io.ucoin.app.model.UcoinTxSignature;
import io.ucoin.app.model.UcoinTxSignatures;

final public class TxSignatures extends SQLiteEntities
        implements UcoinTxSignatures {

    @SuppressWarnings("unused")
    public static final Creator<TxSignatures> CREATOR = new Creator<TxSignatures>() {
        @Override
        public TxSignatures createFromParcel(Parcel in) {
            return new TxSignatures(in);
        }

        @Override
        public TxSignatures[] newArray(int size) {
            return new TxSignatures[size];
        }
    };
    private Long mTxId;

    public TxSignatures(Context context, Long txId) {
        this(context,
                txId,
                SQLiteTable.TxSignature.TX_ID + "=?",
                new String[]{txId.toString()},
                SQLiteTable.TxSignature.ISSUER_ORDER + " ASC");
    }

    private TxSignatures(Context context, Long txId, String selection, String[] selectionArgs) {
        this(context, txId, selection, selectionArgs, null);
    }

    private TxSignatures(Context context, Long txId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.TX_SIGNATURE_URI, selection, selectionArgs, sortOrder);
        mTxId = txId;
    }

    protected TxSignatures(Parcel in) {
        mTxId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinTxSignature add(String signature, Integer issuerOrder) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.TxSignature.TX_ID, mTxId);
        values.put(SQLiteTable.TxSignature.VALUE, signature);
        values.put(SQLiteTable.TxSignature.ISSUER_ORDER, issuerOrder);

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        if (Long.parseLong(uri.getLastPathSegment()) > 0) {
            return new TxSignature(mContext, Long.parseLong(uri.getLastPathSegment()));
        } else {
            return null;
        }
    }

    @Override
    public UcoinTxSignature getById(Long id) {
        return new TxSignature(mContext, id);
    }

    @Override
    public Iterator<UcoinTxSignature> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinTxSignature> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new TxSignature(mContext, id));
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