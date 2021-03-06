package io.ucoin.app.model.sql.sqlite;

import android.content.Context;

import io.ucoin.app.UcoinUris;
import io.ucoin.app.model.UcoinBlocks;
import io.ucoin.app.model.UcoinContacts;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.UcoinPeers;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.UcoinWallets;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;
import io.ucoin.app.technical.crypto.AddressFormatException;

public class Currency extends Row
        implements UcoinCurrency {

    public Currency(Context context, Long id) {
        super(context, UcoinUris.CURRENCY_URI, id);
    }

    @Override
    public String name() {
        return getString(SQLiteTable.Currency.NAME);
    }

    @Override
    public Float c() {
        return getFloat(SQLiteTable.Currency.C);
    }

    @Override
    public Integer dt() {
        return getInt(SQLiteTable.Currency.DT);
    }

    @Override
    public Integer ud0() {
        return getInt(SQLiteTable.Currency.UD0);
    }

    @Override
    public Integer sigDelay() {
        return getInt(SQLiteTable.Currency.SIGDELAY);
    }

    @Override
    public Integer sigValidity() {
        return getInt(SQLiteTable.Currency.SIGVALIDITY);
    }

    @Override
    public Integer sigQty() {
        return getInt(SQLiteTable.Currency.SIGQTY);
    }

    @Override
    public Integer sigWoT() {
        return getInt(SQLiteTable.Currency.SIGWOT);
    }

    @Override
    public Integer msValidity() {
        return getInt(SQLiteTable.Currency.MSVALIDITY);
    }

    @Override
    public Integer stepMax() {
        return getInt(SQLiteTable.Currency.STEPMAX);
    }

    @Override
    public Integer medianTimeBlocks() {
        return getInt(SQLiteTable.Currency.MEDIANTIMEBLOCKS);
    }

    @Override
    public Integer avgGenTime() {
        return getInt(SQLiteTable.Currency.AVGGENTIME);
    }

    @Override
    public Integer dtDiffEval() {
        return getInt(SQLiteTable.Currency.DTDIFFEVAL);
    }

    @Override
    public Integer blocksRot() {
        return getInt(SQLiteTable.Currency.BLOCKSROT);
    }

    @Override
    public Float percentRot() {
        return getFloat(SQLiteTable.Currency.PERCENTROT);
    }

    @Override
    public Long membersCount() {
        return getLong(SQLiteView.Currency.MEMBERS_COUNT);
    }

    @Override
    public Long monetaryMass() {
        return getLong(SQLiteView.Currency.MONETARY_MASS);
    }

    @Override
    public UcoinIdentity identity() {
        return new Identities(mContext, mId).getIdentity();
    }

    @Override
    public UcoinPeers peers() {
        return new Peers(mContext, mId);
    }

    @Override
    public UcoinContacts contacts() {
        return new Contacts(mContext, mId);
    }

    @Override
    public UcoinBlocks blocks() {
        return new Blocks(mContext, mId);
    }

    @Override
    public UcoinIdentity addIdentity(String uid, UcoinWallet wallet) throws AddressFormatException {
        return new Identities(mContext, mId).add(uid, wallet);
    }

    @Override
    public UcoinWallets wallets() {
        return new Wallets(mContext, mId);
    }

    @Override
    public String toString() {
        String s = "CURRENCY id=" + id() + "\n";
        s += "\ncurrency_name=" + name();
        s += "\nc=" + c();
        s += "\ndt=" + dt();
        s += "\nud0=" + ud0();
        s += "\nsigDelay=" + sigDelay();
        s += "\nsigValidity=" + sigValidity();
        s += "\nsigQty=" + sigQty();
        s += "\nsigWoT=" + sigWoT();
        s += "\nmsValidity=" + msValidity();
        s += "\nstepMax=" + stepMax();
        s += "\nmedianTimeBlocks=" + medianTimeBlocks();
        s += "\navgGenTime=" + avgGenTime();
        s += "\ndtDiffEval=" + dtDiffEval();
        s += "\nblocksRot=" + blocksRot();
        s += "\npercentRot=" + percentRot();
        s += "\nidentity=" + identity();

        return s;
    }
}