package vallegrande.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.dto.tts.TtsRequest;
import vallegrande.model.Tts;
import vallegrande.service.TtsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/tts")
public class TtsRest {
    private final TtsService service;

    public TtsRest(TtsService ttsService) {
        this.service = ttsService;
    }

    @PostMapping("/generate")
    public Mono<Tts> generateTts(@RequestBody TtsRequest request) {
        return service.generateAndSave(request.getVoice(), request.getText());
    }

    @GetMapping(value = "/audio/{fileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<byte[]> getAudio(@PathVariable String fileId) {
        return service.getAudio(fileId);
    }

    @GetMapping("/audio/frontend/{fileId}")
    public Mono<String> getAudioForFrontend(@PathVariable String fileId) {
        return service.getAudioForFrontend(fileId);
    }

    @GetMapping("/history")
    public Flux<Tts> getAllTts() {
        return service.getAllTts();
    }

    @GetMapping("/{id}")
    public Mono<Tts> getTtsById(@PathVariable String id) {
        return service.getTtsById(id);
    }

    @PutMapping("/update/{id}")
    public Mono<Tts> updateTts(@PathVariable String id, @RequestBody Tts data) {
        return service.updateTts(id, data);
    }

    @PatchMapping("/delete/{id}")
    public Mono<Tts> deleteTts(@PathVariable String id) {
        return service.deleteTts(id);
    }

    @PatchMapping("/restore/{id}")
    public Mono<Tts> restoreTts(@PathVariable String id) {
        return service.restoreTts(id);
    }
}
