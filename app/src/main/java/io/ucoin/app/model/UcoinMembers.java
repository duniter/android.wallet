package io.ucoin.app.model;

public interface UcoinMembers extends Entities, Iterable<UcoinMember> {
    public UcoinMember newMember(String uid,
                                 String publicKey,
                                 Boolean isMember,
                                 Boolean wasMember,
                                 String self,
                                 Long timestamp);
    public UcoinMember add(UcoinMember member);
    public UcoinMember getById(Long id);
    public UcoinMember getByUid(String uid);
    public UcoinMember getByPublicKey(String publicKey);
    public int delete(Long id);
}