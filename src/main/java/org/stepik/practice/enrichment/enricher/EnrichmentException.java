package org.stepik.practice.enrichment.enricher;

public class EnrichmentException extends RuntimeException {

    public EnrichmentException(final String s, final Throwable e) {
        super(s, e);
    }
}
