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
import io.ucoin.app.model.UcoinTxOutput;
import io.ucoin.app.model.UcoinTxOutputs;
import io.ucoin.app.model.http_api.TxHistory;

final public class TxOutputs extends SQLiteEntities
        implements UcoinTxOutputs {

    @SuppressWarnings("unused")
    public static final Creator<TxOutputs> CREATOR = new Creator<TxOutputs>() {
        @Override
        public TxOutputs createFromParcel(Parcel in) {
            return new TxOutputs(in);
        }

        @Override
        public TxOutputs[] newArray(int size) {
            return new TxOutputs[size];
        }
    };
    private Long mTxId;

    public TxOutputs(Context context, Long txId) {
        this(context,
                txId,
                SQLiteTable.TxOutput.TX_ID + "=?",
                new String[]{txId.toString()},
                null);
    }

    private TxOutputs(Context context, Long txId, String selection, String[] selectionArgs) {
        this(context, txId, selection, selectionArgs, null);
    }

    private TxOutputs(Context context, Long txId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.TX_OUTPUT_URI, selection, selectionArgs, sortOrder);
        mTxId = txId;
    }

    protected TxOutputs(Parcel in) {
        mTxId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinTxOutput add(TxHistory.Output output) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.TxOutput.TX_ID, mTxId);
        values.put(SQLiteTable.TxOutput.PUBLIC_KEY, output.publickKey);
        values.put(SQLiteTable.TxOutput.AMOUNT, output.amount);

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        if (Long.parseLong(uri.getLastPathSegment()) > 0) {
            return new TxOutput(mContext, Long.parseLong(uri.getLastPathSegment()));
        } else {
            return null;
        }
    }

    @Override
    public UcoinTxOutput getById(Long id) {
        return new TxOutput(mContext, id);
    }

    @Override
    public Iterator<UcoinTxOutput> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinTxOutput> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new TxOutput(mContext, id));
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