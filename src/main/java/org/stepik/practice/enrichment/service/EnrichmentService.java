package org.stepik.practice.enrichment.service;

import org.stepik.practice.enrichment.model.Message;

public interface EnrichmentService {
    String enrich(Message message);
}
