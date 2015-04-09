package io.ucoin.app.model;

import io.ucoin.app.enums.SourceType;

public interface UcoinTxInput extends Entity {
    Long txId();
    Integer index();
    SourceType type();
    Long number();
    String fingerprint();
    Long amount();
}

