package vallegrande.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vallegrande.exception.TranslationProcessingException;
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
                                .build()))
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

    @Test
    void testDetectLanguage() {
        String fakeResponse = "{ \"data\": { \"detections\": [{ \"language\": \"es\" }] } }";

        WebClient webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> Mono.just(
                        org.springframework.web.reactive.function.client.ClientResponse
                                .create(org.springframework.http.HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body(fakeResponse)
                                .build()))
                .build();

        DeepTransladeServiceImpl service = new DeepTransladeServiceImpl(webClient, repository);

        StepVerifier.create(service.detectLanguage("Hola"))
                .assertNext(resp -> assertEquals("es", resp.getDetectedLang()))
                .verifyComplete();
    }

    @Test
    void testTranslatePreviewInvalidJsonThrowsException() {
        String invalidResponse = "{ invalid json ";

        WebClient webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> Mono.just(
                        org.springframework.web.reactive.function.client.ClientResponse
                                .create(org.springframework.http.HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body(invalidResponse)
                                .build()))
                .build();

        DeepTransladeServiceImpl service = new DeepTransladeServiceImpl(webClient, repository);

        StepVerifier.create(service.translatePreview("Hello", "en", "es"))
                .expectError(TranslationProcessingException.class)
                .verify();
    }

    @Test
    void testTranslate() {
        String fakeResponse = "{ \"data\": { \"translations\": { \"translatedText\": [\"Hola\"] } } }";

        WebClient webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> Mono.just(
                        org.springframework.web.reactive.function.client.ClientResponse
                                .create(org.springframework.http.HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body(fakeResponse)
                                .build()))
                .build();

        DeepTranslate saved = DeepTranslate.builder()
                .id("1").originalText("Hello").translatedText("Hola")
                .sourceLanguage("en").targetLanguage("es")
                .status(true).createdAt(Instant.now()).build();

        when(repository.save(any())).thenReturn(Mono.just(saved));

        DeepTransladeServiceImpl service = new DeepTransladeServiceImpl(webClient, repository);

        StepVerifier.create(service.translate("Hello", "en", "es"))
                .assertNext(t -> assertEquals("Hola", t.getTranslatedText()))
                .verifyComplete();
    }

    @Test
    void testGetLanguages() {
        String fakeResponse = "{ \"languages\": [ { \"language\": \"en\", \"name\": \"English\" }, { \"language\": \"es\", \"name\": \"Spanish\" } ] }";

        WebClient webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> Mono.just(
                        org.springframework.web.reactive.function.client.ClientResponse
                                .create(org.springframework.http.HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body(fakeResponse)
                                .build()))
                .build();

        DeepTransladeServiceImpl service = new DeepTransladeServiceImpl(webClient, repository);

        StepVerifier.create(service.getLanguages())
                .assertNext(resp -> {
                    assertEquals(2, resp.getLanguages().size());
                    assertEquals("English", resp.getLanguages().get(0).getName());
                    assertEquals("Spanish", resp.getLanguages().get(1).getName());
                })
                .verifyComplete();
    }

    @Test
    void testGetAllTranslations() {
        DeepTranslate t1 = DeepTranslate.builder().id("1").originalText("Hello").translatedText("Hola").build();
        DeepTranslate t2 = DeepTranslate.builder().id("2").originalText("World").translatedText("Mundo").build();

        when(repository.findAll()).thenReturn(Flux.just(t1, t2));

        StepVerifier.create(service.getAllTranslations())
                .expectNext(t1)
                .expectNext(t2)
                .verifyComplete();
    }

    @Test
    void testGetTranslationById() {
        DeepTranslate t1 = DeepTranslate.builder().id("1").originalText("Hello").translatedText("Hola").build();

        when(repository.findById("1")).thenReturn(Mono.just(t1));

        StepVerifier.create(service.getTranslationById("1"))
                .assertNext(t -> assertEquals("Hola", t.getTranslatedText()))
                .verifyComplete();
    }

    @Test
    void testUpdateTranslation() {
        DeepTranslate existing = DeepTranslate.builder()
                .id("1").originalText("Hello").translatedText("Hola")
                .sourceLanguage("en").targetLanguage("es").status(true).build();

        DeepTranslate updated = DeepTranslate.builder()
                .id("1").originalText("Hi").translatedText("Hola")
                .sourceLanguage("en").targetLanguage("es").status(true).build();

        when(repository.findById("1")).thenReturn(Mono.just(existing));
        when(repository.save(any())).thenReturn(Mono.just(updated));

        StepVerifier.create(service.updateTranslation("1", updated))
                .assertNext(t -> assertEquals("Hi", t.getOriginalText()))
                .verifyComplete();
    }

}
