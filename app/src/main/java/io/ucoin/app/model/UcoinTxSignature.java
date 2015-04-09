package io.ucoin.app.model;

public interface UcoinTxSignature extends Entity {
    Long txId();
    String value();
    Integer issuerOrder();
}

