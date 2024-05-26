package org.stepik.practice.enrichment.validation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.stepik.practice.enrichment.model.Message;

@RequiredArgsConstructor
public final class MessageValidatorFacade {

    private final List<Validator<Message>> validators;

    public void validate(final Message message) {
        validators.forEach(validator -> validator.validate(message));
    }
}
