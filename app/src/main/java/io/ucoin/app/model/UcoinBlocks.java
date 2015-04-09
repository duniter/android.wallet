package io.ucoin.app.model;

import io.ucoin.app.model.http_api.BlockchainBlock;

public interface UcoinBlocks extends Entities, Iterable<UcoinBlock> {
    public UcoinBlock add(BlockchainBlock blockchainBlock);
    public UcoinBlock getById(Long id);
    public UcoinBlock firstBlock();
    public UcoinBlock lastBlock();
}
