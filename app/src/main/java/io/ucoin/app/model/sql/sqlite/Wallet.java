package io.ucoin.app.model.sql.sqlite;

import android.content.ContentValues;
import android.content.Context;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.ucoin.app.UcoinUris;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.UcoinSources;
import io.ucoin.app.model.UcoinTxs;
import io.ucoin.app.model.UcoinUds;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;
import io.ucoin.app.technical.crypto.AddressFormatException;

public class Wallet extends Row
        implements UcoinWallet {

    public Wallet(Context context, Long walletId) {
        super(context, UcoinUris.WALLET_URI, walletId);
    }

    @Override
    public Long currencyId() {
        return getLong(SQLiteView.Wallet.CURRENCY_ID);
    }

    @Override
    public String salt() {
        return getString(SQLiteView.Wallet.SALT);
    }

    @Override
    public String publicKey() {
        return getString(SQLiteView.Wallet.PUBLIC_KEY);
    }

    @Override
    public String privateKey() {
        return getString(SQLiteView.Wallet.PRIVATE_KEY);
    }

    @Override
    public String alias() {
        return getString(SQLiteView.Wallet.ALIAS);
    }

    @Override
    public BigInteger quantitativeAmount() {
        return new BigInteger(getString(SQLiteView.Wallet.QUANTITATIVE_AMOUNT));
    }

    @Override
    public BigInteger udValue() {
        return new BigInteger(getString(SQLiteView.Wallet.UD_VALUE));
    }

    @Override
    public Long syncBlock() {
        return getLong(SQLiteView.Wallet.SYNC_BLOCK);
    }

    @Override
    public BigDecimal relativeAmount() {
        return new BigDecimal(getString(SQLiteView.Wallet.RELATIVE_AMOUNT));
    }

    @Override
    public BigDecimal timeAmount() {
        return new BigDecimal(getString(SQLiteView.Wallet.TIME_AMOUNT));
    }

    @Override
    public UcoinSources sources() {
        return new Sources(mContext, mId);
    }

    @Override
    public UcoinTxs txs() {
        return new Txs(mContext, mId);
    }

    @Override
    public UcoinUds uds() {
        return new Uds(mContext, mId);
    }

    @Override
    public UcoinCurrency currency() {
        return new Currency(mContext, currencyId());
    }

    @Override
    public UcoinIdentity identity() {
        return new Identities(mContext, currencyId()).getIdentityByWallet(mId);
    }

    @Override
    public UcoinIdentity addIdentity(String uid, String publicKey) throws AddressFormatException {
        return new Identities(mContext, currencyId()).addWallet(uid, publicKey,mId);
    }

    @Override
    public void setSyncBlock(Long number) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Wallet.SYNC_BLOCK, number);
        update(values);
    }

    @Override
    public String toString() {
        String s = "WALLET id=" + id() + "\n";
        s += "\ncurrencyId=" + currencyId();
        s += "\nsalt=" + salt();
        s += "\npublicKey=" + publicKey();
        s += "\nprivateKey=" + privateKey();
        s += "\nalias=" + alias();
        s += "\nquantitativeAmount=" + quantitativeAmount();
        s += "\nrelativeAmount=" + relativeAmount();
        s += "\ntimeAmount=" + timeAmount();

        return s;
    }
}