package vallegrande.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;
import vallegrande.dto.translate.TranslateRequest;
import vallegrande.dto.translate.TranslateResponse;
import vallegrande.dto.translate.DetectionRequest;
import vallegrande.dto.translate.DetectionResponse;
import vallegrande.dto.translate.LanguagesResponse;
import vallegrande.service.DeepTranslateService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/translate")
public class DeepTranslateRest {

    private final DeepTranslateService service;

    public DeepTranslateRest(DeepTranslateService deepTranslateService) {
        this.service = deepTranslateService;
    }

    @PostMapping
    public Mono<TranslateResponse> translate(@RequestBody TranslateRequest request) {
        return service.translate(request.getText(), request.getSourceLang(), request.getTargetLang())
                .map(entity -> {
                    TranslateResponse resp = new TranslateResponse();
                    resp.setOriginalText(entity.getOriginalText());
                    resp.setSourceLang(entity.getSourceLanguage());
                    resp.setTargetLang(entity.getTargetLanguage());
                    resp.setTranslatedText(entity.getTranslatedText());
                    resp.setCreatedAt(entity.getCreatedAt().toString());
                    return resp;
                });
    }

    @PostMapping("/detect")
    public Mono<DetectionResponse> detectLanguage(@RequestBody DetectionRequest request) {
        return service.detectLanguage(request.getText())
                .map(entity -> {
                    DetectionResponse resp = new DetectionResponse();
                    resp.setText(entity.getText());
                    resp.setDetectedLang(entity.getDetectedLang());
                    return resp;
                });
    }

    @GetMapping("/languages")
    public Mono<LanguagesResponse> getLanguages() {
        return service.getLanguages(); 
    }

}
