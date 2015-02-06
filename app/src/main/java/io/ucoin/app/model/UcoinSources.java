package io.ucoin.app.model;


import io.ucoin.app.model.enums.SourceType;

public interface UcoinSources extends Entities, Iterable<UcoinSource> {
    public UcoinSource newSource(Long communityId,
                                 Integer number,
                                 SourceType type,
                                 String fingerprint,
                                 Long amount);

    public UcoinSource add(UcoinSource source);
    public UcoinSource getById(Long id);
    public int delete(Long id);
}