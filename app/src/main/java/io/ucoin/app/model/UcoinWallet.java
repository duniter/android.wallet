package io.ucoin.app.model;

public interface UcoinWallet extends SqlRow {
    Long currencyId();

    String salt();

    String publicKey();

    String privateKey();

    String alias();

    Double relativeAmount();

    Long quantitativeAmount();

    UcoinSources sources();

    UcoinTxs txs();

    UcoinUds uds();

    UcoinCurrency currency();
}