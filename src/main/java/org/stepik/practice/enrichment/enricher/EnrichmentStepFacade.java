package org.stepik.practice.enrichment.enricher;

import java.util.List;
import org.stepik.practice.enrichment.enricher.implementation.NoOpEnrichmentStep;
import org.stepik.practice.enrichment.model.Message;

public final class EnrichmentStepFacade {

    private final EnrichmentStep chainHead;

    public EnrichmentStepFacade(final List<EnrichmentStep> steps) {
        this.chainHead = ChainElement.buildChain(steps,
                new NoOpEnrichmentStep());
    }

    public Message enrich(final Message message) {
        return chainHead.enrich(message);
    }
}
