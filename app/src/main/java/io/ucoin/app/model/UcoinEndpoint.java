package io.ucoin.app.model;

public interface UcoinEndpoint extends Entity {
    Long peerId();
    String url();
    String ipv4();
    String ipv6();
    Integer port();
}
