package io.ucoin.app.model;

public interface UcoinPeers extends Entities, Iterable<UcoinPeer> {
    public UcoinPeer newPeer(io.ucoin.app.model.http_api.Peer peer);
    public UcoinPeer add(UcoinPeer node);
    public UcoinPeer getById(Long id);
    public int delete(Long id);
}
