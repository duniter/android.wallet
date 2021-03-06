package io.ucoin.app.model;

public interface UcoinContacts extends SqlTable, Iterable<UcoinContact> {
    UcoinContact add(String name, String publicKey);

    UcoinContact getById(Long id);

    UcoinContact getByName(String name);

    UcoinContact getByPublicKey(String publicKey);
}