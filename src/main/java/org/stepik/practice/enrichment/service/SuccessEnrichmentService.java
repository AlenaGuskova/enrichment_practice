package org.stepik.practice.enrichment.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.stepik.practice.enrichment.enricher.Enricher;
import org.stepik.practice.enrichment.model.EnrichmentType;
import org.stepik.practice.enrichment.model.Message;

@RequiredArgsConstructor
public final class SuccessEnrichmentService implements EnrichmentService {

    private final Map<EnrichmentType, Enricher> enrichers;
    @Getter
    private final List<String> successEnrichedMessages =
            Collections.synchronizedList(new LinkedList<>());

    @Override
    public String enrich(final Message message) {
        Enricher enricher = enrichers.get(message.getEnrichmentType());
        String enriched = enricher.enrich(message.getContent());
        successEnrichedMessages.add(enriched);
        return enriched;
    }
}