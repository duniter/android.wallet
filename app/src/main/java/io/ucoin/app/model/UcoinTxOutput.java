package io.ucoin.app.model;

public interface UcoinTxOutput extends Entity {
    Long txId();
    String publicKey();
    Long amount();
}

