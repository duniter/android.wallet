package io.ucoin.app.model;

public interface UcoinIdentity extends Entity {
    Long currencyId();
    Long walletId();

    String uid();
    String self();
    Long timestamp();

    UcoinWallet wallet();
}
