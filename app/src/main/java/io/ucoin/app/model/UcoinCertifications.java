package io.ucoin.app.model;

import io.ucoin.app.enums.CertificationType;
import io.ucoin.app.model.http_api.WotCertification;

public interface UcoinCertifications extends Entities, Iterable<UcoinCertification> {
    public UcoinCertification add(UcoinMember member, CertificationType type, WotCertification.Certification certification);

    UcoinCertification getById(Long id);

    UcoinCertification getBySignature(String signature);
    UcoinCertifications getByType(CertificationType type);
}