package org.stepik.practice.enrichment.enricher;

import java.util.Map;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.stepik.practice.enrichment.FakeUserRepository;
import org.stepik.practice.enrichment.enricher.implementation.MSISDNEnricher;
import org.stepik.practice.enrichment.enricher.implementation.NoOpEnrichmentStep;
import org.stepik.practice.enrichment.model.Message;
import org.stepik.practice.enrichment.model.User;
import org.stepik.practice.enrichment.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stepik.practice.enrichment.model.EnrichmentType.MSISDN;

class MSISDNEnricherTest {

    private static MSISDNEnricher underTest;

    @BeforeAll
    static void setUp() {
        UserRepository userRepository = FakeUserRepository.init(
                Map.of("88005553535", new User("Vasya", "Ivanov"))
        );
        JsonMapper jsonMapper = new JsonMapper();

        underTest = new MSISDNEnricher(jsonMapper, userRepository);
        underTest.setNext(new NoOpEnrichmentStep());
    }

    @Test
    @DisplayName("Should add firstName and lastName into message content by MSISDN")
    void shouldAddUserNamesIntoContent() {
        var content = "{\"msisdn\":\"88005553535\"}";
        var inputMessage = new Message(content, MSISDN);

        var actualMessage = underTest.enrich(inputMessage);
        var expectedContent = "{\"msisdn\":\"88005553535\"," +
                "\"enrichment\":{\"firstName\":\"Vasya\"," +
                "\"lastName\":\"Ivanov\"}}";
        var expectedMessage = new Message(expectedContent, MSISDN);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should return the same message when used data is not found")
    void shouldSameMessageWhenUserDataIsNotFound() {
        var content = "{\"msisdn\":\"12345\"}";
        var inputMessage = new Message(content, MSISDN);

        var actualMessage = underTest.enrich(inputMessage);
        assertThat(actualMessage).isEqualTo(inputMessage);
    }
}