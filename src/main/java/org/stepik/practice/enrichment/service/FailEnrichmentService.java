package org.stepik.practice.enrichment.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.stepik.practice.enrichment.model.Message;
import org.stepik.practice.enrichment.validation.MessageValidatorFacade;

@RequiredArgsConstructor
public final class FailEnrichmentService implements EnrichmentService {

    private final EnrichmentService origin;
    private final MessageValidatorFacade messageValidatorFacade;
    @Getter
    private final List<String> failEnrichedMessages =
            Collections.synchronizedList(new LinkedList<>());

    @Override
    public String enrich(final Message message) {
        try {
            messageValidatorFacade.validate(message);
            return origin.enrich(message);
        } catch (RuntimeException e) {
            Logger.getAnonymousLogger().warning(e.getMessage());
            String content = message.getContent();
            failEnrichedMessages.add(content);
            return content;
        }
    }
}
