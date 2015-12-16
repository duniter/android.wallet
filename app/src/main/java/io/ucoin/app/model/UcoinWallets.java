package io.ucoin.app.model;


import android.database.Cursor;

public interface UcoinWallets extends SqlTable, Iterable<UcoinWallet> {
    UcoinWallet add(String salt, String publicKey, String alias);

    UcoinWallet add(String salt, String publicKey, String privateKey, String alias);

    UcoinWallet getById(Long id);

    UcoinWallet getByPublicKey(String publicKey);

    Cursor getbyCurrency();
}