package io.ucoin.app.model;


import io.ucoin.app.enumeration.TxDirection;
import io.ucoin.app.enumeration.TxState;
import io.ucoin.app.model.http_api.TxHistory;

public interface UcoinTxs extends SqlTable, Iterable<UcoinTx> {
    UcoinTx add(TxHistory.Tx tx, TxDirection direction);

    UcoinTx getById(Long id);

    UcoinTx getLastTx();

    UcoinTxs getByState(TxState state);

    UcoinTxs getByDirection(TxDirection direction);

    UcoinTx getByHash(String hash);
}