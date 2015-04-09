package io.ucoin.app.model;

import io.ucoin.app.enums.EndpointProtocol;

public interface UcoinEndpoint extends Entity {
    Long peerId();
    EndpointProtocol protocol();
    String url();
    String ipv4();
    String ipv6();
    Integer port();
}
