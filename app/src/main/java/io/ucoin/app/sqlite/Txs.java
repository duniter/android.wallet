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
import io.ucoin.app.enums.TxDirection;
import io.ucoin.app.model.UcoinTx;
import io.ucoin.app.model.UcoinTxs;
import io.ucoin.app.model.http_api.TxHistory;

final public class Txs extends SQLiteEntities
        implements UcoinTxs {

    @SuppressWarnings("unused")
    public static final Creator<Txs> CREATOR = new Creator<Txs>() {
        @Override
        public Txs createFromParcel(Parcel in) {
            return new Txs(in);
        }

        @Override
        public Txs[] newArray(int size) {
            return new Txs[size];
        }
    };
    private Long mWalletId;

    public Txs(Context context, Long walletId) {
        this(context, walletId, SQLiteTable.Tx.WALLET_ID + "=?", new String[]{walletId.toString()});
    }

    private Txs(Context context, Long walletId, String selection, String[] selectionArgs) {
        this(context, walletId, selection, selectionArgs, null);
    }

    private Txs(Context context, Long walletId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.TX_URI, selection, selectionArgs, sortOrder);
        mWalletId = walletId;
    }

    protected Txs(Parcel in) {
        mWalletId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinTx add(TxHistory.Tx historyTx, TxDirection direction) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Tx.WALLET_ID, mWalletId);
        values.put(SQLiteTable.Tx.COMMENT, historyTx.comment);
        values.put(SQLiteTable.Tx.HASH, historyTx.hash);
        values.put(SQLiteTable.Tx.BLOCK, historyTx.block_number);
        values.put(SQLiteTable.Tx.TIME, historyTx.time);
        values.put(SQLiteTable.Tx.DIRECTION, direction.name());

        //todo use sql transaction and rollaback in case of failure
        Uri uri = mContext.getContentResolver().insert(mUri, values);
        if (Long.parseLong(uri.getLastPathSegment()) > 0) {
            UcoinTx tx =  new Tx(mContext, Long.parseLong(uri.getLastPathSegment()));
            for (String issuer : historyTx.issuers) {
                int i = 0;
                tx.issuers().add(issuer, i++);
            }

            for (TxHistory.Input input : historyTx.inputs) {
                tx.inputs().add(input);
            }

            for (TxHistory.Output output : historyTx.outputs) {
                tx.outputs().add(output);
            }

            for (String signature : historyTx.signatures) {
                int i = 0;
                tx.signatures().add(signature, i++);
            }

            return tx;
        } else {
            return null;
        }
    }

    @Override
    public UcoinTx getById(Long id) {
        return new Tx(mContext, id);
    }

    @Override
    public Iterator<UcoinTx> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinTx> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new Tx(mContext, id));
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
        if (mWalletId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mWalletId);
        }
    }
}