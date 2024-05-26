package org.stepik.practice.enrichment.service;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.stepik.practice.enrichment.model.Message;
import org.stepik.practice.enrichment.validation.MessageValidatorFacade;
import org.stepik.practice.enrichment.validation.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.stepik.practice.enrichment.model.EnrichmentType.MSISDN;

class FailEnrichmentServiceTest {

    private FailEnrichmentService underTest;
    private MessageValidatorFacade validator;
    private EnrichmentService enrichmentService;

    @BeforeEach
    void setUp() {
        enrichmentService = Mockito.mock(SuccessEnrichmentService.class);
        validator = Mockito.mock(MessageValidatorFacade.class);
        underTest = new FailEnrichmentService(enrichmentService, validator);
    }

    @Test
    @DisplayName("Should return successfully enriched message")
    void shouldReturnEnrichedMessage() {
        var content = """
                {
                    "msisdn": "88005553535"
                }
                """;
        var inputMessage = new Message(content, MSISDN);

        var enriched = """
                {
                    "enrichment": {
                        "firstName": "Vasya",
                        "lastName": "Ivanov"
                    },
                    "msisdn": "88005553535"
                }
                """;
        when(enrichmentService.enrich(inputMessage)).thenReturn(enriched);

        var actual = underTest.enrich(inputMessage);
        assertThat(enriched).isEqualTo(actual);
        assertThat(underTest.getFailEnrichedMessages()).isEmpty();
    }

    @Test
    @DisplayName("Should return the same content when validation does not passed")
    void shouldReturnSameWhenContentIsNoJson() {
        var inputMessage = new Message("{msisdn: 1}", MSISDN);
        doThrow(ValidationException.class).when(validator).validate(inputMessage);

        assertThat(Assertions.assertDoesNotThrow(() -> underTest.enrich(inputMessage)))
                .isEqualTo("{msisdn: 1}");
        assertThat(underTest.getFailEnrichedMessages())
                .hasSize(1)
                .hasSameElementsAs(List.of("{msisdn: 1}"));
    }
}