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
import io.ucoin.app.model.UcoinCurrencies;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.http_api.BlockchainParameter;

final public class Currencies extends SQLiteEntities
        implements UcoinCurrencies {

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

    public Currencies(Context context) {
        super(context, Provider.CURRENCY_URI);
    }

    protected Currencies(Parcel in) {
    }

    @Override
    public UcoinCurrency add(BlockchainParameter parameter) {
        ContentValues values = new ContentValues();

        values.put(SQLiteTable.Currency.CURRENCY_NAME, parameter.currency);
        values.put(SQLiteTable.Currency.C, parameter.c);
        values.put(SQLiteTable.Currency.DT, parameter.dt);
        values.put(SQLiteTable.Currency.UD0, parameter.ud0);
        values.put(SQLiteTable.Currency.SIGDELAY, parameter.sigDelay);
        values.put(SQLiteTable.Currency.SIGVALIDITY, parameter.sigValidity);
        values.put(SQLiteTable.Currency.SIGQTY, parameter.sigQty);
        values.put(SQLiteTable.Currency.SIGWOT, parameter.sigWoT);
        values.put(SQLiteTable.Currency.MSVALIDITY, parameter.msValidity);
        values.put(SQLiteTable.Currency.STEPMAX, parameter.stepMax);
        values.put(SQLiteTable.Currency.MEDIANTIMEBLOCKS, parameter.medianTimeBlocks);
        values.put(SQLiteTable.Currency.AVGGENTIME, parameter.avgGenTime);
        values.put(SQLiteTable.Currency.DTDIFFEVAL, parameter.dtDiffEval);
        values.put(SQLiteTable.Currency.BLOCKSROT, parameter.blocksRot);
        values.put(SQLiteTable.Currency.PERCENTROT, parameter.percentRot);

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        if (Long.parseLong(uri.getLastPathSegment()) > 0) {
            return new Currency(mContext, Long.parseLong(uri.getLastPathSegment()));
        } else {
            return null;
        }

    }

    @Override
    public UcoinCurrency getById(Long id) {
        return new Currency(mContext, id);
    }

    @Override
    public Iterator<UcoinCurrency> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinCurrency> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new Currency(mContext, id));
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
    }
}