package io.ucoin.app.model;


import io.ucoin.app.model.http_api.TxHistory;

public interface UcoinTxOutputs extends Entities, Iterable<UcoinTxOutput> {
    public UcoinTxOutput add(TxHistory.Output output);

    public UcoinTxOutput getById(Long id);
}