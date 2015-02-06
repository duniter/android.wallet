package io.ucoin.app.model;

import io.ucoin.app.model.http_api.BlockchainBlock;
import io.ucoin.app.model.http_api.BlockchainParameter;

public interface UcoinCurrencies extends Entities, Iterable<UcoinCurrency> {
    public UcoinCurrency newCurrency(BlockchainParameter parameter, BlockchainBlock firstBlock,
                                     BlockchainBlock lastBlock);
    public UcoinCurrency add(UcoinCurrency currency);
    public UcoinCurrency getById(Long id);
    public UcoinCurrency getByFirstBlockSignature(String signature);
    public int delete(Long id);
}
