package io.ucoin.app.model;


public interface UcoinTxSignatures extends Entities, Iterable<UcoinTxSignature> {
    public UcoinTxSignature add(String signature, Integer sortOrder);

    public UcoinTxSignature getById(Long id);
}