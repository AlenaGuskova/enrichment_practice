package org.stepik.practice.enrichment.enricher.implementation;

import org.stepik.practice.enrichment.enricher.EnrichmentStep;
import org.stepik.practice.enrichment.model.Message;

/**
 * This implementation is used as a final step in the enrichment steps.
 * It just returns the same message without any actions.
 * The NoOpEnrichmentStep is an example of the Null Object Design pattern.
 */
public class NoOpEnrichmentStep implements EnrichmentStep {

    @Override
    public Message enrich(final Message message) {
        return message;
    }

    @Override
    public void setNext(final EnrichmentStep step) {
    }
}
