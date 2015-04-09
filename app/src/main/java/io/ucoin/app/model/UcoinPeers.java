package io.ucoin.app.model;

import io.ucoin.app.model.http_api.NetworkPeering;

public interface UcoinPeers extends Entities, Iterable<UcoinPeer> {
    public UcoinPeer add(NetworkPeering networkPeering);
    public UcoinPeer getById(Long id);
}
