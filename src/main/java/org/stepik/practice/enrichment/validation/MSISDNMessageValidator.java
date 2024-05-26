package org.stepik.practice.enrichment.validation;

import java.util.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.stepik.practice.enrichment.model.Message;

@RequiredArgsConstructor
public final class MSISDNMessageValidator implements Validator<Message> {

    private static final String FIELD_NAME = "msisdn";

    private final JsonMapper jsonMapper;

    @Override
    public void validate(final Message message) {
        try {
            JsonNode jsonNode = jsonMapper.readTree(message.getContent());
            if (doesNotContainsMSISDField(jsonNode)) {
                throw new ValidationException("It does not contains "
                        + "MSISDN field into message");
            }
        } catch (JsonProcessingException e) {
            Logger.getAnonymousLogger().warning(e.getMessage());
            throw new ValidationException("It's no JSON", e);
        }
    }

    private boolean doesNotContainsMSISDField(final JsonNode jsonNode) {
        return !jsonNode.has(FIELD_NAME);
    }
}
