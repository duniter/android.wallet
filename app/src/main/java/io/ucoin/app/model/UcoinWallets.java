package io.ucoin.app.model;

public interface UcoinWallets extends Entities, Iterable<UcoinWallet> {
    public UcoinWallet newWallet(String salt, String publicKey, String alias);
    public UcoinWallet newWallet(String salt, String publicKey, String privateKey, String alias);
    public UcoinWallet add(UcoinWallet wallet);
    public UcoinWallet getById(Long id);
    public int delete(Long id);
}
