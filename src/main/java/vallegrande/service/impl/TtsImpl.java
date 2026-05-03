package vallegrande.service.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.model.Tts;
import vallegrande.repository.TtsRepository;
import vallegrande.service.TtsService;

@Service
public class TtsImpl implements TtsService {

    private final WebClient webClient;
    private final ReactiveGridFsTemplate gridFsTemplate;
    private final TtsRepository repository;

    public TtsImpl(@Qualifier("ttsWebClient") WebClient webClient, ReactiveGridFsTemplate gridFsTemplate,
            TtsRepository repository) {
        this.webClient = webClient;
        this.gridFsTemplate = gridFsTemplate;
        this.repository = repository;
    }

    @Override
    public Mono<String> saveAudio(byte[] audio, String filename) {
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(audio);
        return gridFsTemplate.store(
                Mono.just(buffer), // Publisher<DataBuffer>
                filename,
                "audio/mpeg").map(ObjectId::toHexString);
    }

    @Override
    public Mono<byte[]> getAudio(String fileId) {
        return gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(fileId))))
                .flatMap(file -> gridFsTemplate.getResource(file) // devuelve Mono<ReactiveGridFsResource>
                        .flatMap(resource -> DataBufferUtils.join(resource.getDownloadStream())
                                .map(dataBuffer -> {
                                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(bytes);
                                    DataBufferUtils.release(dataBuffer);
                                    return bytes;
                                })));
    }

    @Override
    public Mono<String> getAudioForFrontend(String id) {
        return repository.findById(id)
                .flatMap(tts -> getAudio(tts.getAudioFileId())
                        .map(bytes -> {
                            return java.util.Base64.getEncoder().encodeToString(bytes);
                        }));
    }

    @Override
    public Mono<Tts> generateAndSave(String voice, String text) {
        return webClient.post()
                .uri("")
                .bodyValue(Map.of("voice", voice, "text", text))
                .retrieve()
                .bodyToMono(byte[].class)
                .flatMap(audio -> saveAudio(audio, "tts_" + System.currentTimeMillis() + ".mp3")
                        .flatMap(fileId -> {
                            Tts history = new Tts();
                            history.setText(text);
                            history.setVoice(voice);
                            history.setAudioFileId(fileId);
                            history.setCreatedAt(LocalDateTime.now());
                            history.setStatus(true);
                            return repository.save(history);
                        }));
    }

    @Override
    public Flux<Tts> getAllTts() {
        return repository.findAll();
    }

    @Override
    public Mono<Tts> getTtsById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Tts> updateTts(String id, Tts data) {
        return repository.findById(id)
                .flatMap(existing -> {
                    return webClient.post()
                            .uri("")
                            .bodyValue(Map.of("voice", data.getVoice(), "text", data.getText()))
                            .retrieve()
                            .bodyToMono(byte[].class)
                            .flatMap(audioBytes ->
                    saveAudio(audioBytes, "tts_updated_" + System.currentTimeMillis() + ".mp3"))
                            .flatMap(newFileId -> {
                                existing.setText(data.getText());
                                existing.setVoice(data.getVoice());
                                existing.setStatus(data.getStatus());
                                existing.setAudioFileId(newFileId);

                                return repository.save(existing);
                            });
                });
    }

    @Override
    public Mono<Tts> deleteTts(String id) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setStatus(false);
                    return repository.save(existing);
                });
    }

    @Override
    public Mono<Tts> restoreTts(String id) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setStatus(true);
                    return repository.save(existing);
                });
    }
}
