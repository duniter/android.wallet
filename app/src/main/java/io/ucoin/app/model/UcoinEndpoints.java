package io.ucoin.app.model;

public interface UcoinEndpoints extends Entities, Iterable<UcoinEndpoint> {
    public UcoinEndpoint newEndpoint(String endpointStr);
    public UcoinEndpoint add(UcoinEndpoint endpoint);
    public UcoinEndpoint getById(Long id);
    public int delete(Long id);
}
