package io.ucoin.app.model.sql.sqlite;

import android.content.Context;

import io.ucoin.app.content.DbProvider;
import io.ucoin.app.enumeration.SourceType;
import io.ucoin.app.model.UcoinTxInput;
import io.ucoin.app.sqlite.SQLiteTable;

public class TxInput extends Row
        implements UcoinTxInput {

    public TxInput(Context context, Long inputId) {
        super(context, DbProvider.TX_INPUT_URI, inputId);
    }

    @Override
    public Long txId() {
        return getLong(SQLiteTable.TxInput.TX_ID);
    }

    @Override
    public Integer index() {
        return getInt(SQLiteTable.TxInput.ISSUER_INDEX);
    }

    @Override
    public SourceType type() {
        return SourceType.valueOf(getString(SQLiteTable.TxInput.TYPE));
    }

    @Override
    public Long number() {
        return getLong(SQLiteTable.TxInput.NUMBER);
    }

    @Override
    public String fingerprint() {
        return getString(SQLiteTable.TxInput.FINGERPRINT);
    }

    @Override
    public Long amount() {
        return getLong(SQLiteTable.TxInput.AMOUNT);
    }


    @Override
    public String toString() {
        return "TxInput id=" + id() + "\n" +
                "tx_id=" + txId() + "\n" +
                "index=" + index() + "\n" +
                "type=" + type().name() + "\n" +
                "number=" + number() + "\n" +
                "fingerprint=" + fingerprint() + "\n" +
                "amount=" + amount();
    }
}