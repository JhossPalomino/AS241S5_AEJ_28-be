package vallegrande.service.impl;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vallegrande.model.Tts;
import vallegrande.repository.TtsRepository;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import java.time.LocalDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TtsImplTest {

    @Mock
    private ReactiveGridFsTemplate gridFsTemplate;

    @Mock
    private TtsRepository repository;

    private TtsImpl service;

    @BeforeEach
    void setup() {
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(new byte[] { 1, 2, 3 });
        WebClient webClient = WebClient.builder()
                .exchangeFunction(req -> Mono.just(
                        ClientResponse.create(HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body(Flux.just(buffer))
                                .build()))
                .build();

        service = new TtsImpl(webClient, gridFsTemplate, repository);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testSaveAudio() {
        byte[] audio = { 1, 2, 3 };
        when(gridFsTemplate.store(any(Publisher.class), anyString(), anyString()))
                .thenReturn(Mono.just(new ObjectId("507f1f77bcf86cd799439011")));

        StepVerifier.create(service.saveAudio(audio, "test.mp3"))
                .assertNext(id -> assertEquals("507f1f77bcf86cd799439011", id))
                .verifyComplete();
    }

    @Test
    void testGetAudioForFrontend() {
        Tts tts = new Tts();
        tts.setAudioFileId("507f1f77bcf86cd799439011");

        when(repository.findById("1")).thenReturn(Mono.just(tts));

        // Simulamos que getAudio devuelve bytes
        TtsImpl spyService = spy(service);
        doReturn(Mono.just(new byte[] { 1, 2, 3 })).when(spyService).getAudio("507f1f77bcf86cd799439011");

        StepVerifier.create(spyService.getAudioForFrontend("1"))
                .assertNext(base64 -> assertEquals(Base64.getEncoder().encodeToString(new byte[] { 1, 2, 3 }), base64))
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGenerateAndSave() {
        Tts saved = new Tts();
        saved.setId("1");
        saved.setText("Hello");
        saved.setVoice("en");
        saved.setStatus(true);
        saved.setCreatedAt(LocalDateTime.now());

        when(gridFsTemplate.store(any(Publisher.class), anyString(), anyString()))
                .thenReturn(Mono.just(new ObjectId("507f1f77bcf86cd799439011")));
        when(repository.save(any())).thenReturn(Mono.just(saved));

        StepVerifier.create(service.generateAndSave("en", "Hello"))
                .assertNext(t -> assertEquals("Hello", t.getText()))
                .verifyComplete();
    }

    @Test
    void testGetAllTts() {
        Tts t1 = new Tts();
        t1.setText("Hello");
        Tts t2 = new Tts();
        t2.setText("World");

        when(repository.findAll()).thenReturn(Flux.just(t1, t2));

        StepVerifier.create(service.getAllTts())
                .expectNext(t1)
                .expectNext(t2)
                .verifyComplete();
    }

    @Test
    void testGetTtsById() {
        Tts t1 = new Tts();
        t1.setText("Hello");

        when(repository.findById("1")).thenReturn(Mono.just(t1));

        StepVerifier.create(service.getTtsById("1"))
                .assertNext(t -> assertEquals("Hello", t.getText()))
                .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testUpdateTts() {
        Tts existing = new Tts();
        existing.setId("1");
        existing.setText("Old");
        existing.setVoice("en");
        existing.setStatus(true);

        Tts updated = new Tts();
        updated.setId("1");
        updated.setText("New");
        updated.setVoice("en"); 
        updated.setStatus(true); 

        when(repository.findById("1")).thenReturn(Mono.just(existing));
        when(gridFsTemplate.store(any(Publisher.class), anyString(), anyString()))
                .thenReturn(Mono.just(new ObjectId("507f1f77bcf86cd799439011")));
        when(repository.save(any())).thenReturn(Mono.just(updated));

        StepVerifier.create(service.updateTts("1", updated))
                .assertNext(t -> assertEquals("New", t.getText()))
                .verifyComplete();
    }

    @Test
    void testDeleteTts() {
        Tts existing = new Tts();
        existing.setId("1");
        existing.setStatus(true);
        Tts deleted = new Tts();
        deleted.setId("1");
        deleted.setStatus(false);

        when(repository.findById("1")).thenReturn(Mono.just(existing));
        when(repository.save(any())).thenReturn(Mono.just(deleted));

        StepVerifier.create(service.deleteTts("1"))
                .assertNext(t -> assertEquals(false, t.getStatus()))
                .verifyComplete();
    }

    @Test
    void testRestoreTts() {
        Tts existing = new Tts();
        existing.setId("1");
        existing.setStatus(false);
        Tts restored = new Tts();
        restored.setId("1");
        restored.setStatus(true);

        when(repository.findById("1")).thenReturn(Mono.just(existing));
        when(repository.save(any())).thenReturn(Mono.just(restored));

        StepVerifier.create(service.restoreTts("1"))
                .assertNext(t -> assertEquals(true, t.getStatus()))
                .verifyComplete();
    }
}
