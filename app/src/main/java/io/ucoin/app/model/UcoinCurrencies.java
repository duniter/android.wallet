package io.ucoin.app.model;

import io.ucoin.app.model.http_api.BlockchainParameter;
import io.ucoin.app.model.http_api.NetworkPeering;

public interface UcoinCurrencies extends SqlTable, Iterable<UcoinCurrency> {
    UcoinCurrency add(BlockchainParameter parameter, NetworkPeering peer);

    UcoinCurrency getById(Long id);
}
