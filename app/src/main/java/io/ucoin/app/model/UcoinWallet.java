package io.ucoin.app.model;

public interface UcoinWallet extends Entity{
    Long currencyId();
    String salt();
    String publicKey();
    String privateKey();
    String alias();
    Long relativeBalance();
    Long quantitativeBalance();
    UcoinSources sources();
    UcoinSources newSources(io.ucoin.app.model.http_api.Sources sources);
}