package io.ucoin.app.model.sql.sqlite;

import android.content.Context;

import io.ucoin.app.UcoinUris;
import io.ucoin.app.enumeration.DayOfWeek;
import io.ucoin.app.enumeration.Month;
import io.ucoin.app.model.UcoinUd;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.sqlite.SQLiteView;

public class Ud extends Row
        implements UcoinUd {

    public Ud(Context context, Long udId) {
        super(context, UcoinUris.UD_URI, udId);
    }

    @Override
    public Long walletId() {
        return getLong(SQLiteView.Ud.WALLET_ID);
    }

    @Override
    public Long block() {
        return getLong(SQLiteView.Ud.BLOCK);
    }

    @Override
    public Boolean consumed() {
        return getBoolean(SQLiteView.Ud.CONSUMED);
    }
    @Override
    public Long time() {
        return getLong(SQLiteView.Ud.TIME);
    }

    @Override
    public String currencyName() {
        return getString(SQLiteView.Ud.CURRENCY_NAME);
    }

    @Override
    public Integer year() {
        return getInt(SQLiteView.Ud.YEAR);
    }

    @Override
    public Month month() {
        return Month.fromInt(getInt(SQLiteView.Ud.MONTH));
    }

    @Override
    public DayOfWeek dayOfWeek() {
        try {
            return DayOfWeek.fromInt(getInt(SQLiteView.Ud.DAY_OF_WEEK));
        } catch (Exception e) {
            return DayOfWeek.UNKNOWN;
        }
    }

    @Override
    public Integer day() {
        //Need to parse with radix for not to be treated as octal when starting with a 0
        String day = getString(SQLiteView.Ud.DAY);
        if (day != null)
            return Integer.parseInt(day);
        else
            return 0;
    }

    @Override
    public String hour() {
        return getString(SQLiteView.Ud.HOUR);
    }

    @Override
    public Long quantitativeAmount() {
        return getLong(SQLiteView.Ud.QUANTITATIVE_AMOUNT);
    }

    @Override
    public Double relativeAmountThen() {
        return getDouble(SQLiteView.Ud.RELATIVE_AMOUNT_THEN);
    }

    @Override
    public Double relativeAmountNow() {
        return getDouble(SQLiteView.Ud.RELATIVE_AMOUNT_NOW);
    }

    @Override
    public UcoinWallet wallet() {
        return new Wallet(mContext, walletId());
    }

    @Override
    public String toString() {
        return "Ud id=" + id() + "\n" +
                "wallet_id=" + walletId() + "\n" +
                "block=" + block() + "\n" +
                "consumed=" + consumed() + "\n" +
                "time=" + time() + "\n" +
                "amount=" + quantitativeAmount();
    }
}