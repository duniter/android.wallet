package io.ucoin.app.model;

import io.ucoin.app.model.http_api.BlockchainParameter;

public interface UcoinCurrencies extends Entities, Iterable<UcoinCurrency> {
    public UcoinCurrency add(BlockchainParameter parameter);
    public UcoinCurrency getById(Long id);
}
