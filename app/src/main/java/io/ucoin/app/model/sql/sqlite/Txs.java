package io.ucoin.app.model.sql.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.DbProvider;
import io.ucoin.app.enumeration.TxDirection;
import io.ucoin.app.enumeration.TxState;
import io.ucoin.app.model.UcoinTx;
import io.ucoin.app.model.UcoinTxs;
import io.ucoin.app.model.http_api.TxHistory;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;

final public class Txs extends Table
        implements UcoinTxs {

    private Long mWalletId;

    public Txs(Context context, Long walletId) {
        this(context, walletId, SQLiteTable.Tx.WALLET_ID + "=?", new String[]{walletId.toString()});
    }

    private Txs(Context context, Long walletId, String selection, String[] selectionArgs) {
        this(context, walletId, selection, selectionArgs, null);
    }

    private Txs(Context context, Long walletId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, DbProvider.TX_URI, selection, selectionArgs, sortOrder);
        mWalletId = walletId;
    }

    @Override
    public UcoinTx add(TxHistory.Tx tx, TxDirection direction) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Tx.WALLET_ID, mWalletId);
        values.put(SQLiteTable.Tx.VERSION, tx.version);
        values.put(SQLiteTable.Tx.COMMENT, tx.comment);
        values.put(SQLiteTable.Tx.DIRECTION, direction.name());

        if (tx instanceof TxHistory.ConfirmedTx) {
            values.put(SQLiteTable.Tx.HASH, ((TxHistory.ConfirmedTx) tx).hash);
            values.put(SQLiteTable.Tx.TIME, ((TxHistory.ConfirmedTx) tx).time);
            values.put(SQLiteTable.Tx.BLOCK, ((TxHistory.ConfirmedTx) tx).block_number);
            values.put(SQLiteTable.Tx.STATE, TxState.CONFIRMED.name());
        } else if (tx instanceof TxHistory.PendingTx) {
            values.put(SQLiteTable.Tx.HASH, ((TxHistory.PendingTx) tx).hash);
            values.put(SQLiteTable.Tx.STATE, TxState.PENDING.name());
        }


        //todo use sql transaction and rollback in case of failure
        Uri uri = insert(values);
        if (Long.parseLong(uri.getLastPathSegment()) > 0) {
            UcoinTx newTx = new Tx(mContext, Long.parseLong(uri.getLastPathSegment()));

            for (String issuer : tx.issuers) {
                newTx.issuers().add(issuer, newTx.issuers().count());
            }

            for (TxHistory.Tx.Input input : tx.inputs) {
                newTx.inputs().add(input);
            }

            for (TxHistory.Tx.Output output : tx.outputs) {
                newTx.outputs().add(output);
            }

            if (tx.signatures != null) {
                for (String signature : tx.signatures) {
                    newTx.signatures().add(signature, newTx.signatures().count());
                }
            }

            return newTx;
        } else {
            return null;
        }
    }

    @Override
    public UcoinTx getById(Long id) {
        return new Tx(mContext, id);
    }

    @Override
    public UcoinTx getLastTx() {
        String selection = SQLiteView.Tx.WALLET_ID + "=?";
        String[] selectionArgs = new String[]{mWalletId.toString()};
        String sortOrder = SQLiteView.Tx.TIME + " DESC LIMIT 1";
        UcoinTxs txs = new Txs(mContext, mWalletId, selection, selectionArgs, sortOrder);
        if (txs.iterator().hasNext()) {
            return txs.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public UcoinTxs getByDirection(TxDirection direction) {
        String selection = SQLiteTable.Tx.WALLET_ID + "=? AND " +
                SQLiteTable.Tx.DIRECTION + "=?";
        String[] selectionArgs = new String[]{mWalletId.toString(), direction.name()};
        return new Txs(mContext, mWalletId, selection, selectionArgs);
    }

    @Override
    public UcoinTx getByHash(String hash) {
        String selection = SQLiteView.Tx.WALLET_ID + "=? AND " + SQLiteView.Tx.HASH + "=?";
        String[] selectionArgs = new String[]{
                mWalletId.toString(),
                hash
        };
        String sortOrder = SQLiteView.Tx.TIME + " DESC LIMIT 1";
        UcoinTxs txs = new Txs(mContext, mWalletId, selection, selectionArgs, sortOrder);
        if (txs.iterator().hasNext()) {
            return txs.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public UcoinTxs getByState(TxState state) {
        String selection = SQLiteTable.Tx.WALLET_ID + "=? AND " +
                SQLiteTable.Tx.STATE + "=?";
        String[] selectionArgs = new String[]{mWalletId.toString(), state.name()};
        return new Txs(mContext, mWalletId, selection, selectionArgs);
    }

    @Override
    public Iterator<UcoinTx> iterator() {
        Cursor cursor = fetch();
        if (cursor != null) {
            ArrayList<UcoinTx> data = new ArrayList<>();
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                data.add(new Tx(mContext, id));
            }
            cursor.close();

            return data.iterator();
        }
        return null;
    }
}