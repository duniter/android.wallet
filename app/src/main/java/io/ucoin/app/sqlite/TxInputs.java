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
import io.ucoin.app.model.UcoinTxInput;
import io.ucoin.app.model.UcoinTxInputs;
import io.ucoin.app.model.http_api.TxHistory;

final public class TxInputs extends SQLiteEntities
        implements UcoinTxInputs {

    @SuppressWarnings("unused")
    public static final Creator<TxInputs> CREATOR = new Creator<TxInputs>() {
        @Override
        public TxInputs createFromParcel(Parcel in) {
            return new TxInputs(in);
        }

        @Override
        public TxInputs[] newArray(int size) {
            return new TxInputs[size];
        }
    };
    private Long mTxId;

    public TxInputs(Context context, Long txId) {
        this(context,
                txId,
                SQLiteTable.TxInput.TX_ID + "=?",
                new String[]{txId.toString()},
                SQLiteTable.TxInput.ISSUER_INDEX + " ASC");
    }

    private TxInputs(Context context, Long txId, String selection, String[] selectionArgs) {
        this(context, txId, selection, selectionArgs, null);
    }

    private TxInputs(Context context, Long txId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.TX_INPUT_URI, selection, selectionArgs, sortOrder);
        mTxId = txId;
    }

    protected TxInputs(Parcel in) {
        mTxId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinTxInput add(TxHistory.Input input) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.TxInput.TX_ID, mTxId);
        values.put(SQLiteTable.TxInput.ISSUER_INDEX, input.index);
        values.put(SQLiteTable.TxInput.TYPE, input.type.name());
        values.put(SQLiteTable.TxInput.NUMBER, input.number);
        values.put(SQLiteTable.TxInput.FINGERPRINT, input.fingerprint);
        values.put(SQLiteTable.TxInput.AMOUNT, input.amount);

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        if (Long.parseLong(uri.getLastPathSegment()) > 0) {
            return new TxInput(mContext, Long.parseLong(uri.getLastPathSegment()));
        } else {
            return null;
        }
    }

    @Override
    public UcoinTxInput getById(Long id) {
        return new TxInput(mContext, id);
    }

    @Override
    public Iterator<UcoinTxInput> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinTxInput> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new TxInput(mContext, id));
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