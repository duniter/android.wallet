package io.ucoin.app.model;

public interface UcoinTxOutput extends SqlRow {
    Long txId();

    String publicKey();

    Long amount();
}

