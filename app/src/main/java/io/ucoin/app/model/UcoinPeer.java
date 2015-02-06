package io.ucoin.app.model;

public interface UcoinPeer extends Entity {
    Long currencyId();
    String publicKey();
    String block();
    String signature();
    UcoinEndpoints endpoints();
}
