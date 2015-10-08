package io.ucoin.app.model.sql.sqlite;

import android.content.Context;

import io.ucoin.app.content.DbProvider;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinSources;
import io.ucoin.app.model.UcoinTxs;
import io.ucoin.app.model.UcoinUds;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.sqlite.SQLiteView;

public class Wallet extends Row
        implements UcoinWallet {

    public Wallet(Context context, Long walletId) {
        super(context, DbProvider.WALLET_URI, walletId);
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
    public Long quantitativeAmount() {
        return getLong(SQLiteView.Wallet.QUANTITATIVE_AMOUNT);
    }

    @Override
    public Double relativeAmount() {
        return getDouble(SQLiteView.Wallet.RELATIVE_AMOUNT);
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
    public String toString() {
        String s = "WALLET id=" + id() + "\n";
        s += "\ncurrencyId=" + currencyId();
        s += "\nsalt=" + salt();
        s += "\npublicKey=" + publicKey();
        s += "\nprivateKey=" + privateKey();
        s += "\nalias=" + alias();
        s += "\nquantitativeAmount=" + quantitativeAmount();
        s += "\nrelativeAmountNow=" + relativeAmount();

        return s;
    }
}