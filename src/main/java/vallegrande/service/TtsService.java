package vallegrande.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.model.Tts;

public interface TtsService {

    Mono<String> saveAudio(byte[] audio, String filename);
    Mono<byte[]> getAudio(String fileId);
    Mono<Tts> generateAndSave(String text, String voice);
    Flux<Tts> getAllTts();
    Mono<Tts> getTtsById(String id);
    Mono<Tts> updateTts(String id, Tts data);
    Mono<Tts> deleteTts(String id);
    Mono<Tts> restoreTts(String id);

}
