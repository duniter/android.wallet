package io.ucoin.app.model;

import io.ucoin.app.enumeration.SourceState;
import io.ucoin.app.enumeration.SourceType;


public interface UcoinSource extends SqlRow {
    Long walletId();

    Long number();

    SourceType type();

    String fingerprint();

    Long amount();

    SourceState state();

    void setState(SourceState state);
}

