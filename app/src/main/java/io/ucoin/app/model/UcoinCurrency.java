package io.ucoin.app.model;

public interface UcoinCurrency extends Entity{

    Long identityId();

    String currencyName();
    Float c();
    Integer dt();
    Integer ud0();
    Integer sigDelay();
    Integer sigValidity();
    Integer sigQty();
    Integer sigWoT();
    Integer msValidity();
    Integer stepMax();
    Integer medianTimeBlocks();
    Integer avgGenTime();
    Integer dtDiffEval();
    Integer blocksRot();
    Float percentRot();

    UcoinIdentity identity();
    
    UcoinBlocks blocks();
    UcoinWallets wallets();
    UcoinPeers peers();
    UcoinMembers members();

    void identityId(Long id);
    public UcoinIdentity newIdentity(Long walletId, String uid);
    public UcoinIdentity setIdentity(UcoinIdentity identity);
}
