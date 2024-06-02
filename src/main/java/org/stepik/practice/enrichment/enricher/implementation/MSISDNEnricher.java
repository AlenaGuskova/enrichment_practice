package org.stepik.practice.enrichment.enricher.implementation;

import java.io.IOException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.stepik.practice.enrichment.enricher.AbstractEnrichmentStep;
import org.stepik.practice.enrichment.model.Message;
import org.stepik.practice.enrichment.repository.UserRepository;

@RequiredArgsConstructor
public final class MSISDNEnricher extends AbstractEnrichmentStep {

    private static final String FIELD_NAME = "msisdn";

    private final JsonMapper jsonMapper;
    private final UserRepository userRepository;

    @Override
    protected Optional<Message> enrichAndApplyNext(final Message message)
            throws IOException {
        var jsonNode = (ObjectNode) jsonMapper.readTree(message.getContent());
        var msisdn = jsonNode.get(FIELD_NAME).asText();
        var user = userRepository.findByMSISDN(msisdn)
                .orElseThrow(() -> new NoSuchElementException("No an user "
                        + "with msisdn=" + msisdn));
        var enrichment = new Enrichment(
                user.getFirstName(),
                user.getLastName()
        );
        var propertyName = enrichment.getClass()
                .getSimpleName()
                .toLowerCase(Locale.ROOT);
        jsonNode.putPOJO(propertyName, enrichment);

        var newContent = jsonMapper.writeValueAsString(jsonNode);
        return Optional.of(new Message(newContent, message.getEnrichmentType()));
    }

    @Value
    static class Enrichment {

        String firstName;
        String lastName;
    }
}
