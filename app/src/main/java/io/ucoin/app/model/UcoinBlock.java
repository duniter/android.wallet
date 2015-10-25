package io.ucoin.app.model;

public interface UcoinBlock extends SqlRow {
    Long currencyId();

    Integer version();

    Long nonce();

    Long number();

    Long powMin();

    Long time();

    Long medianTime();

    Long dividend();

    Long monetaryMass();

    String issuer();

    String previousHash();

    String previousIssuer();

    Long membersCount();

    Boolean isMembership();

    String hash();

    String signature();

    void setIsMembership(Boolean isMembership);

    boolean remove();

    UcoinCurrency currency();
}