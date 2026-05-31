package vallegrande.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import vallegrande.dto.translate.DetectionResponse;
import vallegrande.dto.translate.LanguageResponse;
import vallegrande.dto.translate.LanguagesResponse;
import vallegrande.exception.TranslationProcessingException;
import vallegrande.model.DeepTranslate;
import vallegrande.repository.DeepTranslateRepository;
import vallegrande.service.DeepTranslateService;

@Service
public class DeepTransladeServiceImpl implements DeepTranslateService {

    private final WebClient webClient;
    private final DeepTranslateRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public DeepTransladeServiceImpl(@Qualifier("translateWebClient") WebClient webClient,
                                    DeepTranslateRepository repository) {
        this.webClient = webClient;
        this.repository = repository;
    }

    // 🔹 Helper genérico para parsear con manejo de excepción
    private <T> T safeParse(String response, Parser<T> parser) {
        try {
            return parser.parse(response);
        } catch (JsonProcessingException e) {
            throw new TranslationProcessingException("Error parsing response", e);
        }
    }

    @FunctionalInterface
    private interface Parser<T> {
        T parse(String response) throws JsonProcessingException;
    }

    private DeepTranslate buildTranslation(String response, String text, String sourceLang, String targetLang)
            throws JsonProcessingException {
        JsonNode translatedArray = mapper.readTree(response)
                .path("data").path("translations").path("translatedText");

        String translatedText = translatedArray.isArray() && translatedArray.size() > 0
                ? translatedArray.get(0).asText()
                : "";

        return DeepTranslate.builder()
                .sourceLanguage(sourceLang)
                .targetLanguage(targetLang)
                .originalText(text)
                .translatedText(translatedText)
                .status(true)
                .createdAt(Instant.now())
                .build();
    }

    private DetectionResponse buildDetection(String response) throws JsonProcessingException {
        JsonNode detectionNode = mapper.readTree(response)
                .path("data").path("detections").get(0);
        return new DetectionResponse(null, detectionNode.path("language").asText());
    }

    private LanguagesResponse buildLanguages(String response) throws JsonProcessingException {
        List<LanguageResponse> list = new ArrayList<>();
        for (JsonNode langNode : mapper.readTree(response).path("languages")) {
            list.add(new LanguageResponse(
                    langNode.path("language").asText(),
                    langNode.path("name").asText()
            ));
        }
        return new LanguagesResponse(list);
    }

    // 🔹 Métodos públicos
    @Override
    public Mono<DeepTranslate> translate(String text, String sourceLang, String targetLang) {
        return webClient.post()
                .uri("")
                .bodyValue(Map.of("q", text, "source", sourceLang, "target", targetLang))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> safeParse(response, r -> buildTranslation(r, text, sourceLang, targetLang)))
                .flatMap(repository::save);
    }

    @Override
    public Mono<DeepTranslate> translatePreview(String text, String sourceLang, String targetLang) {
        return webClient.post()
                .uri("")
                .bodyValue(Map.of("q", text, "source", sourceLang, "target", targetLang))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> safeParse(response, r -> buildTranslation(r, text, sourceLang, targetLang)));
    }

    @Override
    public Mono<DetectionResponse> detectLanguage(String text) {
        return webClient.post()
                .uri("/detect")
                .bodyValue(Map.of("q", text))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> safeParse(response, this::buildDetection));
    }

    @Override
    public Mono<LanguagesResponse> getLanguages() {
        return webClient.get()
                .uri("/languages")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> safeParse(response, this::buildLanguages));
    }

    @Override
    public Flux<DeepTranslate> getAllTranslations() {
        return repository.findAll();
    }

    @Override
    public Mono<DeepTranslate> getTranslationById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<DeepTranslate> updateTranslation(String id, DeepTranslate data) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setOriginalText(data.getOriginalText());
                    existing.setTranslatedText(data.getTranslatedText());
                    existing.setSourceLanguage(data.getSourceLanguage());
                    existing.setTargetLanguage(data.getTargetLanguage());
                    existing.setStatus(data.getStatus());
                    return repository.save(existing);
                });
    }

    @Override
    public Mono<DeepTranslate> deleteTranslation(String id) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setStatus(false);
                    return repository.save(existing);
                });
    }

    @Override
    public Mono<DeepTranslate> restoreTts(String id) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setStatus(true);
                    return repository.save(existing);
                });
    }
}
