package org.stepik.practice.enrichment.model;

import lombok.Value;

@Value
public class Message {

    String content;
    EnrichmentType enrichmentType;
}
