package io.ucoin.app.model;

import io.ucoin.app.enums.EndpointProtocol;
import io.ucoin.app.model.http_api.NetworkPeering;
import io.ucoin.app.sqlite.Endpoints;

public interface UcoinEndpoints extends Entities, Iterable<UcoinEndpoint> {
    public UcoinEndpoint add(NetworkPeering.Endpoint endpoint);
    public UcoinEndpoint getById(Long id);
    public Endpoints getByProtocol(EndpointProtocol protocol);
}
