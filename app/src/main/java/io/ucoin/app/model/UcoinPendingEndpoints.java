package io.ucoin.app.model;

public interface UcoinPendingEndpoints extends Entities, Iterable<UcoinPendingEndpoint> {
    public UcoinPendingEndpoint add(String address, Integer port);
}
