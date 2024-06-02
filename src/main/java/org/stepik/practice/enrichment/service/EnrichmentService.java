package org.stepik.practice.enrichment.service;

import org.stepik.practice.enrichment.model.Message;

public interface EnrichmentService {
    Message enrich(Message message);
}
