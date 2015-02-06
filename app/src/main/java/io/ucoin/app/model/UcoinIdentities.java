package io.ucoin.app.model;

public interface UcoinIdentities extends Entities, Iterable<UcoinIdentity> {
    public UcoinIdentity newIdentity(Long walletId, String uid);
    public UcoinIdentity add(UcoinIdentity identity);
    public UcoinIdentity getById(Long id);
    public int delete(Long id);
}
