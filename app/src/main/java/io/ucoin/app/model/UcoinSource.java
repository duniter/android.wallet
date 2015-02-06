package io.ucoin.app.model;

import io.ucoin.app.model.enums.SourceType;

public interface UcoinSource extends Entity {
    Long walletId();
    Integer number();
    SourceType type();
    String fingerprint();
    Long amount();
}

