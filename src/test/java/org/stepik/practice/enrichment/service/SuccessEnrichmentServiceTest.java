package org.stepik.practice.enrichment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.stepik.practice.enrichment.enricher.EnrichmentStepFacade;
import org.stepik.practice.enrichment.model.Message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.stepik.practice.enrichment.model.EnrichmentType.MSISDN;

class SuccessEnrichmentServiceTest {

    private EnrichmentServiceImpl underTest;
    private EnrichmentStepFacade enrichmentStepFacade;

    @BeforeEach
    void setUp() {
        enrichmentStepFacade = Mockito.mock(EnrichmentStepFacade.class);
        underTest = new EnrichmentServiceImpl(enrichmentStepFacade);
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

        var expectedContent = """
                {
                    "enrichment": {
                        "firstName": "Vasya",
                        "lastName": "Ivanov"
                    },
                    "msisdn": "88005553535"
                }
                """;
        var expectedMessage = new Message(expectedContent, MSISDN);
        when(enrichmentStepFacade.enrich(inputMessage))
                .thenReturn(expectedMessage);

        var actual = underTest.enrich(inputMessage);
        assertThat(actual).isEqualTo(expectedMessage);
        assertThat(underTest.getSuccessEnrichedMessages()).hasSize(1);
    }
}