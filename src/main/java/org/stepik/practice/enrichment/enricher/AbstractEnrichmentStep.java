package org.stepik.practice.enrichment.enricher;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;
import org.stepik.practice.enrichment.model.Message;

public abstract class AbstractEnrichmentStep implements EnrichmentStep {

    private EnrichmentStep next;

    @Override
    public final void setNext(final EnrichmentStep step) {
        this.next = step;
    }

    @Override
    public final Message enrich(final Message message) {
        try {
            return enrichAndApplyNext(message)
                    .map(enrichedMessage -> next.enrich(enrichedMessage))
                    .orElseGet(() -> next.enrich(message));
        } catch (Exception e) {
            Logger.getAnonymousLogger().severe("Unexpected error "
                    + "during enrichment for msg: " + message);
            return next.enrich(message);
        }
    }

    protected abstract Optional<Message> enrichAndApplyNext(Message message)
            throws IOException;
}
