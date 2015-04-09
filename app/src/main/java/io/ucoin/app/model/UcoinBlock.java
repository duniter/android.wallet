package io.ucoin.app.model;

public interface UcoinBlock extends Entity{
Long currencyId();
    Integer number();
    Long monetaryMass();
    String signature();
    }
