package io.ucoin.app.model;


import io.ucoin.app.model.http_api.TxHistory;

public interface UcoinTxInputs extends Entities, Iterable<UcoinTxInput> {
    public UcoinTxInput add(TxHistory.Input input);

    public UcoinTxInput getById(Long id);
}