package vallegrande.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vallegrande.model.DeepTranslate;
import vallegrande.repository.DeepTranslateRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DeepTransladeServiceImplTest {

    @Test
    void testTranslatePreview() {
        String fakeResponse = "{ \"data\": { \"translations\": { \"translatedText\": [\"Hola\"] } } }";

        DeepTranslateRepository repository = mock(DeepTranslateRepository.class);

        WebClient webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> Mono.just(
                        org.springframework.web.reactive.function.client.ClientResponse
                                .create(org.springframework.http.HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body(fakeResponse)
                                .build()
                ))
                .build();

        DeepTransladeServiceImpl service = new DeepTransladeServiceImpl(webClient, repository);

        Mono<DeepTranslate> result = service.translatePreview("Hello", "en", "es");

        StepVerifier.create(result)
            .assertNext(t -> {
                assertEquals("Hola", t.getTranslatedText());
                assertEquals("en", t.getSourceLanguage());
                assertEquals("es", t.getTargetLanguage());
            })
            .verifyComplete();
    }
}
