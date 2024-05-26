package org.stepik.practice.enrichment.enricher;

import org.stepik.practice.enrichment.model.EnrichmentType;

public interface Enricher {

    String enrich(String content);

    EnrichmentType getType();
}
