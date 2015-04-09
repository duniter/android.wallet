package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.UcoinSources;
import io.ucoin.app.model.http_api.TxSources;

final public class Sources extends SQLiteEntities
        implements UcoinSources {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Sources> CREATOR = new Parcelable.Creator<Sources>() {
        @Override
        public Sources createFromParcel(Parcel in) {
            return new Sources(in);
        }

        @Override
        public Sources[] newArray(int size) {
            return new Sources[size];
        }
    };
    private Long mWalletId;

    public Sources(Context context, Long walletId) {
        this(context, walletId, SQLiteTable.Source.WALLET_ID + "=?", new String[]{walletId.toString()});
    }

    private Sources(Context context, Long walletId, String selection, String selectionArgs[]) {
        this(context, walletId, selection, selectionArgs, null);
    }

    private Sources(Context context, Long walletId, String selection, String selectionsArgs[], String sortOrder) {
        super(context, Provider.SOURCE_URI, selection, selectionsArgs, sortOrder);
        mWalletId = walletId;
    }


    protected Sources(Parcel in) {
        mWalletId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinSource add(TxSources.Source source) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Source.WALLET_ID, mWalletId);
        values.put(SQLiteTable.Source.TYPE, source.type.name());
        values.put(SQLiteTable.Source.FINGERPRINT, source.fingerprint);
        values.put(SQLiteTable.Source.NUMBER, source.number);
        values.put(SQLiteTable.Source.AMOUNT, source.amount);

        Uri uri = mContext.getContentResolver().insert(mUri, values);
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
        ArrayList<UcoinSource> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new Source(mContext, id));
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