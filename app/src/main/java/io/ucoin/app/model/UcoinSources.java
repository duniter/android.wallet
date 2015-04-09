package io.ucoin.app.model;


import io.ucoin.app.model.http_api.TxSources;

public interface UcoinSources extends Entities, Iterable<UcoinSource> {
    public UcoinSource add(TxSources.Source source);

    public UcoinSource getById(Long id);
}