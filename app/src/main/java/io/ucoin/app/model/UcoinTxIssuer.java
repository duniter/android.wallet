package io.ucoin.app.model;

public interface UcoinTxIssuer extends Entity {
    Long txId();
    String publicKey();
    Integer issuerOrder();
}

