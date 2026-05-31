package vallegrande.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vallegrande.model.DeepTranslate;
import vallegrande.repository.DeepTranslateRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
class DeepTransladeServiceImplTest {

    @Mock
    private DeepTranslateRepository repository;
    private DeepTransladeServiceImpl service;

    @BeforeEach
    void setup() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> Mono.just(
                        org.springframework.web.reactive.function.client.ClientResponse
                                .create(org.springframework.http.HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body("{}")
                                .build()
                ))
                .build();

        service = new DeepTransladeServiceImpl(webClient, repository);
    }

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
                                .build()))
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

    @Test
    void testDeleteTranslation() {
        DeepTranslate existing = DeepTranslate.builder()
                .id("1")
                .originalText("Hello")
                .translatedText("Hola")
                .sourceLanguage("en")
                .targetLanguage("es")
                .status(true)
                .createdAt(Instant.now())
                .build();

        DeepTranslate deleted = DeepTranslate.builder()
                .id("1")
                .originalText("Hello")
                .translatedText("Hola")
                .sourceLanguage("en")
                .targetLanguage("es")
                .status(false)
                .createdAt(Instant.now())
                .build();

        when(repository.findById("1")).thenReturn(Mono.just(existing));
        when(repository.save(any())).thenReturn(Mono.just(deleted));

        Mono<DeepTranslate> result = service.deleteTranslation("1");

        StepVerifier.create(result)
                .assertNext(t -> assertEquals(false, t.getStatus()))
                .verifyComplete();
    }

    @Test
    void testRestoreTts() {
        DeepTranslate existing = DeepTranslate.builder()
                .id("1")
                .originalText("Hello")
                .translatedText("Hola")
                .sourceLanguage("en")
                .targetLanguage("es")
                .status(false) 
                .createdAt(Instant.now())
                .build();

        DeepTranslate restored = DeepTranslate.builder()
                .id("1")
                .originalText("Hello")
                .translatedText("Hola")
                .sourceLanguage("en")
                .targetLanguage("es")
                .status(true) 
                .createdAt(Instant.now())
                .build();

        when(repository.findById("1")).thenReturn(Mono.just(existing));
        when(repository.save(any())).thenReturn(Mono.just(restored));

        Mono<DeepTranslate> result = service.restoreTts("1");

        StepVerifier.create(result)
                .assertNext(t -> assertEquals(true, t.getStatus()))
                .verifyComplete();
    }

}
