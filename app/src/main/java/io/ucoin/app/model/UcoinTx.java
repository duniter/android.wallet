package io.ucoin.app.model;

import io.ucoin.app.enums.TxDirection;

public interface UcoinTx extends Entity {
    Long walletId();
    String comment();
    String hash();
    Long block();
    Long time();
    TxDirection direction();

    UcoinTxIssuers issuers();
    UcoinTxInputs inputs();
    UcoinTxOutputs outputs();
    UcoinTxSignatures signatures();
}

