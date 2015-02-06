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

    Long membersCount();
    String firstBlockSignature();

    UcoinIdentity identity();

    UcoinIdentities identities();
    UcoinWallets wallets();
    UcoinPeers peers();

    void identityId(Long id);
}
