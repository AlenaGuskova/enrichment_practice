package org.stepik.practice.enrichment.enricher;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.stepik.practice.enrichment.model.EnrichmentType;
import org.stepik.practice.enrichment.model.User;
import org.stepik.practice.enrichment.repository.UserRepository;

import static org.stepik.practice.enrichment.model.EnrichmentType.MSISDN;

@RequiredArgsConstructor
public final class MSISDNEnricher implements Enricher {

    private static final String FIELD_NAME = "msisdn";

    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;

    @Override
    public String enrich(final String content) {
        try {
            ObjectNode jsonNode = (ObjectNode) jsonMapper.readTree(content);
            String msisdn = jsonNode.get(FIELD_NAME).asText();
            User user = userRepository.findByMSISDN(msisdn)
                    .orElseThrow(() -> new NoSuchElementException("No an user "
                            + "with msisdn=" + msisdn));
            Enrichment enrichment = new Enrichment(user.getFirstName(),
                    user.getLastName()
            );
            String propertyName = enrichment.getClass()
                    .getSimpleName()
                    .toLowerCase(Locale.ROOT);
            jsonNode.putPOJO(propertyName, enrichment);
            return jsonMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            Logger.getAnonymousLogger().warning(e.getMessage());
            throw new EnrichmentException("There was a problem with "
                    + "the content when parsing it", e);
        }
    }

    @Override
    public EnrichmentType getType() {
        return MSISDN;
    }

    @Value
    static class Enrichment {

        String firstName;
        String lastName;
    }
}
