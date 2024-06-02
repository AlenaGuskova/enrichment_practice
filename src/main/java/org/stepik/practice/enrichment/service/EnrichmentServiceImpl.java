package org.stepik.practice.enrichment.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.stepik.practice.enrichment.enricher.EnrichmentStepFacade;
import org.stepik.practice.enrichment.model.Message;

@RequiredArgsConstructor
public final class EnrichmentServiceImpl implements EnrichmentService {

    private final EnrichmentStepFacade enrichmentStepFacade;
    @Getter
    private final List<Message> successEnrichedMessages =
            Collections.synchronizedList(new LinkedList<>());
    @Getter
    private final List<Message> failEnrichedMessages =
            Collections.synchronizedList(new LinkedList<>());

    @Override
    public Message enrich(final Message message) {
        Message enriched = enrichmentStepFacade.enrich(message);
        if (message.equals(enriched)) {
            failEnrichedMessages.add(message);
        } else {
            successEnrichedMessages.add(enriched);
        }
        return enriched;
    }
}
