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
import org.stepik.practice.enrichment.enricher.EnrichmentStep;
import org.stepik.practice.enrichment.enricher.EnrichmentStepFacade;
import org.stepik.practice.enrichment.enricher.implementation.MSISDNEnricher;
import org.stepik.practice.enrichment.model.EnrichmentType;
import org.stepik.practice.enrichment.model.Message;
import org.stepik.practice.enrichment.model.User;
import org.stepik.practice.enrichment.repository.UserRepository;
import org.stepik.practice.enrichment.service.EnrichmentServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

class FullEnrichmentTest {

    private EnrichmentServiceImpl underTest;

    @BeforeEach
    void setUp() {
        UserRepository userRepository = FakeUserRepository.init(
                Map.of("88005553535", new User("Vasya", "Ivanov"))
        );
        JsonMapper jsonMapper = new JsonMapper();

        EnrichmentStep enricher = new MSISDNEnricher(jsonMapper, userRepository);
        EnrichmentStepFacade enrichmentStepFacade =
                new EnrichmentStepFacade(List.of(enricher));

        underTest = new EnrichmentServiceImpl(enrichmentStepFacade);
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
                    underTest.enrich(inputMessage);
                    latch.countDown();
                }));
        latch.await();

        assertThat(underTest.getFailEnrichedMessages())
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
                    underTest.enrich(inputMessage);
                    latch.countDown();
                }));
        latch.await();

        assertThat(underTest.getSuccessEnrichedMessages())
                .hasSize(numberOfThreads);
    }
}