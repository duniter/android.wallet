package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.UcoinSources;
import io.ucoin.app.model.enums.SourceType;

final public class Sources extends SQLiteEntities
        implements UcoinSources {

    private Long mWalletId;
    private ArrayList<UcoinSource> mSourcesArray;

    public Sources(Context context, Long walletId) {
        super(context, Provider.SOURCE_URI);
        mWalletId = walletId;
    }

    public Sources(Long walletId, ArrayList<UcoinSource> sources) {
        mWalletId = walletId;
        if (sources == null) {
            mSourcesArray = new ArrayList<>();
        } else {
            mSourcesArray = sources;
        }
    }

    @Override
    public UcoinSource newSource(Integer number, SourceType type, String fingerprint, Long amount) {
        return new Source(mWalletId, number, type, fingerprint, amount);
    }

    @Override
    public UcoinSource add(UcoinSource source) {
        if (mContext != null) {
            ContentValues values = new ContentValues();
            values.put(Contract.Source.WALLET_ID, source.walletId());
            values.put(Contract.Source.TYPE, source.type().name());
            values.put(Contract.Source.FINGERPRINT, source.fingerprint());
            values.put(Contract.Source.NUMBER, source.number());
            values.put(Contract.Source.AMOUNT, source.amount());

            Uri uri = mContext.getContentResolver().insert(mUri, values);
            return getById(Long.parseLong(uri.getLastPathSegment()));
        } else {
            mSourcesArray.add(source);
            return source;
        }
    }


    @Override
    public UcoinSource getById(Long id) {
        return new Source(mContext, id);
    }

    @Override
    public Iterator<UcoinSource> iterator() {
        if (mContext == null) {
            return mSourcesArray.iterator();
        }
        String selection = Contract.Source.WALLET_ID + "=?";
        String[] selectionArgs = new String[]{mWalletId.toString()};
        final Cursor sourcesCursor = mContext.getContentResolver().query(mUri, null,
                selection, selectionArgs, null);

        return new Iterator<UcoinSource>() {
            @Override
            public boolean hasNext() {
                if (sourcesCursor.moveToNext())
                    return true;
                else {
                    sourcesCursor.close();
                    return false;
                }
            }

            @Override
            public UcoinSource next() {
                Long id = sourcesCursor.getLong(sourcesCursor.getColumnIndex(Contract.Source._ID));
                return new Source(mContext, id);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected Sources(Parcel in) {
        mWalletId = in.readByte() == 0x00 ? null : in.readLong();
        if (in.readByte() == 0x01) {
            mSourcesArray = new ArrayList<>();
            in.readList(mSourcesArray, UcoinSource.class.getClassLoader());
        } else {
            mSourcesArray = null;
        }
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
        if (mSourcesArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mSourcesArray);
        }
    }

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
}