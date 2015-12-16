package io.ucoin.app.model;

public interface UcoinWallet extends SqlRow {
    Long currencyId();

    String salt();

    String publicKey();

    String privateKey();

    String alias();

    Double relativeAmount();

    Double timeAmount();

    Long quantitativeAmount();

    Long syncBlock();

    UcoinSources sources();

    UcoinTxs txs();

    UcoinUds uds();

    UcoinCurrency currency();

    void setSyncBlock(Long number);
}