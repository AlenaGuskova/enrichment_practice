package org.stepik.practice.enrichment.enricher;

import java.util.Map;
import java.util.NoSuchElementException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.stepik.practice.enrichment.FakeUserRepository;
import org.stepik.practice.enrichment.model.User;
import org.stepik.practice.enrichment.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MSISDNEnricherTest {

    private static MSISDNEnricher underTest;

    @BeforeAll
    static void setUp() {
        UserRepository userRepository = FakeUserRepository.init(
                Map.of("88005553535", new User("Vasya", "Ivanov"))
        );
        JsonMapper jsonMapper = new JsonMapper();

        underTest = new MSISDNEnricher(jsonMapper, userRepository);
    }

    @Test
    @DisplayName("Should add firstName and lastName into message content by MSISDN")
    void shouldAddUserNamesIntoContent() {
        var content = "{\"msisdn\":\"88005553535\"}";
        var actual = underTest.enrich(content);
        var expected = "{\"msisdn\":\"88005553535\"," +
                "\"enrichment\":{\"firstName\":\"Vasya\"," +
                "\"lastName\":\"Ivanov\"}}";
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @DisplayName("Should throw an exception when used data is not found")
    void shouldThrowExceptionWhenUserDataIsNotFound() {
        var content = "{\"msisdn\":\"12345\"}";
        assertThatThrownBy(() -> underTest.enrich(content))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("No an user with msisdn=12345");
    }

    @Test
    @DisplayName("Should throw an exception when content is no JSON")
    void shouldReturnSameWhenContentIsNoJson() {
        assertThatThrownBy(() -> underTest.enrich("{msisdn: 1}"))
                .isInstanceOf(EnrichmentException.class)
                .hasMessageContaining("There was a problem with the content when parsing it")
                .hasCauseInstanceOf(JsonProcessingException.class);
    }
}