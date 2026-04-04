package vallegrande.service;

import reactor.core.publisher.Mono;
import vallegrande.model.Tts;

public interface TtsService {

    Mono<String> saveAudio(byte[] audio, String filename);
    Mono<byte[]> getAudio(String fileId);
    Mono<Tts> generateAndSave(String text, String voice);

}
