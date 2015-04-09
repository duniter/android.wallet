package io.ucoin.app.model;


import io.ucoin.app.enums.TxDirection;
import io.ucoin.app.model.http_api.TxHistory;

public interface UcoinTxs extends Entities, Iterable<UcoinTx> {
    public UcoinTx add(TxHistory.Tx tx, TxDirection direction);

    public UcoinTx getById(Long id);
}