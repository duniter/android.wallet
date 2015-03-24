package io.ucoin.app.model;

public interface UcoinMember extends Entity {
    Long currencyId();
    String uid();
    String publicKey();
    Boolean isMember();
    Boolean wasMember();
    String self();
    Long timestamp();

    void isMember(Boolean is);
    void wasMember(Boolean was);
}

