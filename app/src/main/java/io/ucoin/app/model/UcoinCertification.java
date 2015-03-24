package io.ucoin.app.model;

import io.ucoin.app.model.enums.CertificationType;

public interface UcoinCertification extends Entity {
    Long identityId();
    Long memberId();
    CertificationType type();
    Long block();
    Long medianTime();
    String signature();
    UcoinMember member();
}