package io.ucoin.app.model;


import io.ucoin.app.enumeration.SourceState;
import io.ucoin.app.model.http_api.TxSources;

public interface UcoinSources extends SqlTable, Iterable<UcoinSource> {
    UcoinSource add(TxSources.Source source);

    UcoinSource getById(Long id);

    UcoinSources getByState(SourceState state);

    UcoinSource getByFingerprint(String fingerprint);
}