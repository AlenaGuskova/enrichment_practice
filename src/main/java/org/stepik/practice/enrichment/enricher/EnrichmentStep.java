package org.stepik.practice.enrichment.enricher;

import org.stepik.practice.enrichment.model.Message;

public interface EnrichmentStep extends ChainElement<EnrichmentStep> {

    Message enrich(Message message);
}
