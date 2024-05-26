package org.stepik.practice.enrichment.service;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.stepik.practice.enrichment.enricher.Enricher;
import org.stepik.practice.enrichment.enricher.EnrichmentException;
import org.stepik.practice.enrichment.model.EnrichmentType;
import org.stepik.practice.enrichment.model.Message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.stepik.practice.enrichment.model.EnrichmentType.MSISDN;

class SuccessEnrichmentServiceTest {

    private SuccessEnrichmentService underTest;
    private Map<EnrichmentType, Enricher> enrichers;

    @BeforeEach
    void setUp() {
        enrichers = Mockito.mock(Map.class);
        underTest = new SuccessEnrichmentService(enrichers);
    }

    @Test
    @DisplayName("Should return successfully enriched message add into message list")
    void shouldReturnEnrichedMessageAndAddIntoMessageList() {
        var content = """
                {
                    "msisdn": "88005553535"
                }
                """;
        var inputMessage = new Message(content, MSISDN);

        Enricher enricher = Mockito.mock(Enricher.class);
        when(enrichers.get(MSISDN)).thenReturn(enricher);
        var expected = """
                {
                    "enrichment": {
                        "firstName": "Vasya",
                        "lastName": "Ivanov"
                    },
                    "msisdn": "88005553535"
                }
                """;
        when(enricher.enrich(content)).thenReturn(expected);

        var actual = underTest.enrich(inputMessage);
        assertThat(expected).isEqualTo(actual);
        assertThat(underTest.getSuccessEnrichedMessages()).hasSize(1);
    }

    @Test
    @DisplayName("Should throw an exception and not add into message list")
    void shouldThrowExceptionAndNotAddIntoMessageList() {
        String content = "{msisdn: 1}";
        var inputMessage = new Message(content, MSISDN);

        Enricher enricher = Mockito.mock(Enricher.class);
        when(enrichers.get(MSISDN)).thenReturn(enricher);
        when(enricher.enrich(content)).thenThrow(EnrichmentException.class);

        assertThatThrownBy(() -> underTest.enrich(inputMessage))
                .isInstanceOf(EnrichmentException.class);
        assertThat(underTest.getSuccessEnrichedMessages()).isEmpty();
    }
}