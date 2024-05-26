package org.stepik.practice.enrichment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.stepik.practice.enrichment.enricher.Enricher;
import org.stepik.practice.enrichment.enricher.MSISDNEnricher;
import org.stepik.practice.enrichment.model.EnrichmentType;
import org.stepik.practice.enrichment.model.Message;
import org.stepik.practice.enrichment.model.User;
import org.stepik.practice.enrichment.repository.UserRepository;
import org.stepik.practice.enrichment.service.FailEnrichmentService;
import org.stepik.practice.enrichment.service.SuccessEnrichmentService;
import org.stepik.practice.enrichment.validation.MSISDNMessageValidator;
import org.stepik.practice.enrichment.validation.MessageValidatorFacade;

import static org.assertj.core.api.Assertions.assertThat;

class FullEnrichmentTest {

    private FailEnrichmentService failEnrichmentService;
    private SuccessEnrichmentService successEnrichmentService;

    @BeforeEach
    void setUp() {
        UserRepository userRepository = FakeUserRepository.init(
                Map.of("88005553535", new User("Vasya", "Ivanov"))
        );
        JsonMapper jsonMapper = new JsonMapper();

        MSISDNEnricher enricher = new MSISDNEnricher(jsonMapper, userRepository);
        Map<EnrichmentType, Enricher> enrichers =
                Map.of(enricher.getType(), enricher);

        successEnrichmentService = new SuccessEnrichmentService(enrichers);
        MessageValidatorFacade validator = new MessageValidatorFacade(
                List.of(new MSISDNMessageValidator(jsonMapper))
        );
        failEnrichmentService = new FailEnrichmentService(successEnrichmentService, validator);
    }

    @Test
    @DisplayName("Should not enrich a message content with concurrency when it has no JSON format")
    public void shouldNotEnrichMessageContentWhenItHasNoJSONFormat() throws InterruptedException {
        var numberOfThreads = 10;
        var service = Executors.newFixedThreadPool(numberOfThreads);
        var latch = new CountDownLatch(numberOfThreads);
        var inputMessage = new Message("{msisdn: 1}", EnrichmentType.MSISDN);

        IntStream.range(0, numberOfThreads)
                .forEach(i -> service.submit(() -> {
                    failEnrichmentService.enrich(inputMessage);
                    latch.countDown();
                }));
        latch.await();

        assertThat(failEnrichmentService.getFailEnrichedMessages())
                .hasSize(numberOfThreads);
    }

    @Test
    @DisplayName("Should enrich a message content successfully with concurrency")
    public void shouldEnrichMessageContentSuccessfullyWithConcurrency() throws InterruptedException {
        var numberOfThreads = 10;
        var service = Executors.newFixedThreadPool(numberOfThreads);
        var latch = new CountDownLatch(numberOfThreads);

        var content = """
                {
                    "msisdn": "88005553535"
                }
                """;
        var inputMessage = new Message(content, EnrichmentType.MSISDN);

        IntStream.range(0, numberOfThreads)
                .forEach(i -> service.submit(() -> {
                    successEnrichmentService.enrich(inputMessage);
                    latch.countDown();
                }));
        latch.await();

        assertThat(successEnrichmentService.getSuccessEnrichedMessages())
                .hasSize(numberOfThreads);
    }
}