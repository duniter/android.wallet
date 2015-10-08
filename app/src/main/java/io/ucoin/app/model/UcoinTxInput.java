package io.ucoin.app.model;

import io.ucoin.app.enumeration.SourceType;

public interface UcoinTxInput extends SqlRow {
    Long txId();

    Integer index();

    SourceType type();

    Long number();

    String fingerprint();

    Long amount();
}

