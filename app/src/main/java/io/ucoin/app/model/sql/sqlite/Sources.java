package io.ucoin.app.model.sql.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.DbProvider;
import io.ucoin.app.enumeration.SourceState;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.UcoinSources;
import io.ucoin.app.model.http_api.TxSources;
import io.ucoin.app.sqlite.SQLiteTable;

final public class Sources extends Table
        implements UcoinSources {

    private Long mWalletId;

    public Sources(Context context, Long walletId) {
        this(context, walletId, SQLiteTable.Source.WALLET_ID + "=?", new String[]{walletId.toString()});
    }

    private Sources(Context context, Long walletId, String selection, String selectionArgs[]) {
        this(context, walletId, selection, selectionArgs, null);
    }

    private Sources(Context context, Long walletId, String selection, String selectionsArgs[], String sortOrder) {
        super(context, DbProvider.SOURCE_URI, selection, selectionsArgs, sortOrder);
        mWalletId = walletId;
    }

    @Override
    public UcoinSource add(TxSources.Source source) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Source.WALLET_ID, mWalletId);
        values.put(SQLiteTable.Source.TYPE, source.type.name());
        values.put(SQLiteTable.Source.FINGERPRINT, source.fingerprint);
        values.put(SQLiteTable.Source.NUMBER, source.number);
        values.put(SQLiteTable.Source.AMOUNT, source.amount);
        values.put(SQLiteTable.Source.STATE, SourceState.AVAILABLE.name());

        Uri uri = insert(values);
        if (Long.parseLong(uri.getLastPathSegment()) > 0) {
            return new Source(mContext, Long.parseLong(uri.getLastPathSegment()));
        } else {
            return null;
        }
    }

    @Override
    public UcoinSource getById(Long id) {
        return new Source(mContext, id);
    }

    @Override
    public Iterator<UcoinSource> iterator() {
        Cursor cursor = fetch();
        if (cursor != null) {
            ArrayList<UcoinSource> data = new ArrayList<>();
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                data.add(new Source(mContext, id));
            }
            cursor.close();

            return data.iterator();
        }
        return null;
    }


    @Override
    public UcoinSources getByState(SourceState state) {
        String selection = SQLiteTable.Tx.WALLET_ID + "=? AND " +
                SQLiteTable.Source.STATE + "=?";
        String[] selectionArgs = new String[]{mWalletId.toString(), state.name()};
        return new Sources(mContext, mWalletId, selection, selectionArgs);
    }

    @Override
    public UcoinSource getByFingerprint(String fingerprint) {
        String selection = SQLiteTable.Tx.WALLET_ID + "=? AND " +
                SQLiteTable.Source.FINGERPRINT + "=?";
        String[] selectionArgs = new String[]{mWalletId.toString(), fingerprint};
        UcoinSources sources = new Sources(mContext, mWalletId, selection, selectionArgs);
        if (sources.iterator().hasNext()) {
            return sources.iterator().next();
        } else {
            return null;
        }
    }
}