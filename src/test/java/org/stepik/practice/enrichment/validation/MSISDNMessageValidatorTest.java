package org.stepik.practice.enrichment.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.stepik.practice.enrichment.model.Message;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.stepik.practice.enrichment.model.EnrichmentType.MSISDN;

class MSISDNMessageValidatorTest {

    private final MSISDNMessageValidator underTest =
            new MSISDNMessageValidator(new JsonMapper());

    @Test
    @DisplayName("Should not throw any exception when all is ok")
    void shouldRNotThrowException() {
        var content = """
                {
                    "msisdn": "12345"
                }
                """;
        var inputMessage = new Message(content, MSISDN);

        assertDoesNotThrow(() -> underTest.validate(inputMessage));
    }

    @Test
    @DisplayName("Should throw an exception when MSISDN field missed")
    void shouldThrowExceptionWhenMSISDNMissed() {
        var content = """ 
                {
                    "action": "button_click",
                    "page": "book_card"
                }""";
        var inputMessage = new Message(content, MSISDN);

        assertThatThrownBy(() -> underTest.validate(inputMessage))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("It does not contains MSISDN field into message");
    }

    @Test
    @DisplayName("Should throw an exception when content is no JSON")
    void shouldThrowExceptionWhenContentIsNoJson() {
        var inputMessage = new Message("{msisdn: 1}", MSISDN);

        assertThatThrownBy(() -> underTest.validate(inputMessage))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("It's no JSON")
                .hasCauseInstanceOf(JsonProcessingException.class);
    }
}