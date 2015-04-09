package io.ucoin.app.model;

public interface UcoinWallets extends Entities, Iterable<UcoinWallet> {
    public UcoinWallet add(String salt, String publicKey, String alias);
    public UcoinWallet add(String salt, String publicKey, String privateKey, String alias);
    public UcoinWallet getById(Long id);
    public UcoinWallet getByPublicKey(String publicKey);
}
