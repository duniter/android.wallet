package io.ucoin.app.model;

import io.ucoin.app.model.enums.CertificationType;

public interface UcoinCertifications extends Entities, Iterable<UcoinCertification>  {
    public UcoinCertification newCertification(UcoinMember member,
                                              CertificationType type,
                                              Long block,
                                              Long medianTime,
                                              String signature);
    public UcoinCertification add(UcoinCertification certification);
    public int delete(Long id);
    UcoinCertification getById(Long id);
    UcoinCertification getBySignature(String signature);
}