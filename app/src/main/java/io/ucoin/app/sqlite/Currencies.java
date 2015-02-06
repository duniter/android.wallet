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
import io.ucoin.app.model.UcoinCurrencies;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinPeer;
import io.ucoin.app.model.http_api.BlockchainBlock;
import io.ucoin.app.model.http_api.BlockchainParameter;

final public class Currencies extends SQLiteEntities
        implements UcoinCurrencies {

    private ArrayList<UcoinCurrency> mCommunitiesArray;

    public Currencies(Context context) {
        super(context, Provider.CURRENCY_URI);
    }

    public Currencies(ArrayList<UcoinCurrency> communities) {
        if (communities == null) {
            mCommunitiesArray = new ArrayList<>();
        } else {
            mCommunitiesArray = communities;
        }
    }

    public UcoinCurrency newCurrency(BlockchainParameter parameter, BlockchainBlock firstBlock,
                                     BlockchainBlock lastBlock) {
        return new Currency(parameter, firstBlock, lastBlock);
    }

    @Override
    public UcoinCurrency add(UcoinCurrency currency) {
        ContentValues values = new ContentValues();
        if(currency.identity() != null) {
            values.put(Contract.Currency.IDENTITY_ID, currency.identity().id());
        }
        values.put(Contract.Currency.CURRENCY_NAME, currency.currencyName());
        values.put(Contract.Currency.C, currency.c());
        values.put(Contract.Currency.DT, currency.dt());
        values.put(Contract.Currency.UD0, currency.ud0());
        values.put(Contract.Currency.SIGDELAY, currency.sigDelay());
        values.put(Contract.Currency.SIGVALIDITY, currency.sigValidity());
        values.put(Contract.Currency.SIGQTY, currency.sigQty());
        values.put(Contract.Currency.SIGWOT, currency.sigWoT());
        values.put(Contract.Currency.MSVALIDITY, currency.msValidity());
        values.put(Contract.Currency.STEPMAX, currency.stepMax());
        values.put(Contract.Currency.MEDIANTIMEBLOCKS, currency.medianTimeBlocks());
        values.put(Contract.Currency.AVGGENTIME, currency.avgGenTime());
        values.put(Contract.Currency.DTDIFFEVAL, currency.dtDiffEval());
        values.put(Contract.Currency.BLOCKSROT, currency.blocksRot());
        values.put(Contract.Currency.PERCENTROT, currency.percentRot());

        values.put(Contract.Currency.MEMBERS_COUNT, currency.membersCount());
        values.put(Contract.Currency.FIRST_BLOCK_SIGNATURE, currency.firstBlockSignature());

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        UcoinCurrency sqlCurrency = getById(Long.parseLong(uri.getLastPathSegment()));

        for (UcoinPeer peer : currency.peers()) {
            peer = new Peer(sqlCurrency.id(), peer);
            sqlCurrency.peers().add(peer);
        }

        return sqlCurrency;
    }

    @Override
    public UcoinCurrency getById(Long id) {
        return new Currency(mContext, id);
    }

    @Override
    public UcoinCurrency getByFirstBlockSignature(String signature) {
        Long id = queryUnique(
                Contract.Currency.FIRST_BLOCK_SIGNATURE + "=?",
                new String[]{signature});

        if (id == null)
            return null;

        return new Currency(mContext, id);
    }


    @Override
    public Iterator<UcoinCurrency> iterator() {
        if (mContext == null) {
            return mCommunitiesArray.iterator();
        }

        final Cursor communitiesCursor = mContext.getContentResolver().query(mUri, null,
                null, null, null);

        return new Iterator<UcoinCurrency>() {
            @Override
            public boolean hasNext() {
                if (communitiesCursor.moveToNext())
                    return true;
                else {
                    communitiesCursor.close();
                    return false;
                }
            }

            @Override
            public UcoinCurrency next() {
                Long id = communitiesCursor.getLong(communitiesCursor.getColumnIndex(Contract.Currency._ID));
                return new Currency(mContext, id);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected Currencies(Parcel in) {
        if (in.readByte() == 0x01) {
            mCommunitiesArray = new ArrayList<UcoinCurrency>();
            in.readList(mCommunitiesArray, UcoinCurrency.class.getClassLoader());
        } else {
            mCommunitiesArray = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mCommunitiesArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mCommunitiesArray);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Currencies> CREATOR = new Parcelable.Creator<Currencies>() {
        @Override
        public Currencies createFromParcel(Parcel in) {
            return new Currencies(in);
        }

        @Override
        public Currencies[] newArray(int size) {
            return new Currencies[size];
        }
    };
}