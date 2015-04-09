package io.ucoin.app.model;


public interface UcoinTxIssuers extends Entities, Iterable<UcoinTxIssuer> {
    public UcoinTxIssuer add(String publicKey, Integer sortOrder);

    public UcoinTxIssuer getById(Long id);
}