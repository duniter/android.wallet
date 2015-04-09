package io.ucoin.app.model;

public interface UcoinWallet extends Entity{
    Long currencyId();
    String currencyName();
    String salt();
    String publicKey();
    String privateKey();
    String alias();
    Float relativeAmount();
    Long quantitativeAmount();
    UcoinSources sources();
    UcoinTxs txs();
}