package vallegrande.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
}
