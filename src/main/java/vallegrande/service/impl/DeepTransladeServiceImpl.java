package vallegrande.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import vallegrande.dto.translate.DetectionResponse;
import vallegrande.dto.translate.LanguageResponse;
import vallegrande.dto.translate.LanguagesResponse;
import vallegrande.model.DeepTranslate;
import vallegrande.repository.DeepTranslateRepository;
import vallegrande.service.DeepTranslateService;
import reactor.core.publisher.Flux;

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

    @Override
    public Mono<DeepTranslate> translate(String text, String sourceLang, String targetLang) {
        return webClient.post()
                .uri("") // baseUrl ya incluye /language/translate/v2
                .bodyValue(Map.of("q", text, "source", sourceLang, "target", targetLang))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode json = mapper.readTree(response);
                        JsonNode translatedArray = json.path("data")
                                .path("translations")
                                .path("translatedText");

                        String translatedText = "";
                        if (translatedArray.isArray() && translatedArray.size() > 0) {
                            translatedText = translatedArray.get(0).asText();
                        }

                        DeepTranslate translation = new DeepTranslate();
                        translation.setSourceLanguage(sourceLang);
                        translation.setTargetLanguage(targetLang);
                        translation.setOriginalText(text);
                        translation.setTranslatedText(translatedText);
                        translation.setStatus(true);
                        translation.setCreatedAt(Instant.now());

                        return translation;
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing translation response", e);
                    }
                })
                .flatMap(repository::save);
    }

    @Override
    public Mono<DeepTranslate> translatePreview(String text, String sourceLang, String targetLang) {
        return webClient.post()
                .uri("") // baseUrl ya incluye /language/translate/v2
                .bodyValue(Map.of("q", text, "source", sourceLang, "target", targetLang))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode json = mapper.readTree(response);
                        
                        // 👇 USAMOS LA MISMA LÓGICA DE EXTRACCIÓN QUE EN TRANSLATE
                        JsonNode translatedArray = json.path("data")
                                .path("translations")
                                .path("translatedText");

                        String translatedText = "";
                        if (translatedArray.isArray() && translatedArray.size() > 0) {
                            translatedText = translatedArray.get(0).asText();
                        }

                        DeepTranslate translation = new DeepTranslate();
                        translation.setSourceLanguage(sourceLang);
                        translation.setTargetLanguage(targetLang);
                        translation.setOriginalText(text);
                        translation.setTranslatedText(translatedText);
                        translation.setStatus(true);
                        translation.setCreatedAt(Instant.now());

                        // Aquí NO se guarda en la base, solo se devuelve para previsualizar
                        return translation;
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing translation response", e);
                    }
                });
    }

    @Override
    public Mono<DetectionResponse> detectLanguage(String text) {
        return webClient.post()
                .uri("/detect")
                .bodyValue(Map.of("q", text))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode json = mapper.readTree(response);
                        JsonNode detectionNode = json.path("data").path("detections").get(0);

                        DetectionResponse resp = new DetectionResponse();
                        resp.setDetectedLang(detectionNode.path("language").asText());
                        return resp;
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing detection response", e);
                    }
                });
    }

    @Override
    public Mono<LanguagesResponse> getLanguages() {
        return webClient.get()
                .uri("/languages")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode json = mapper.readTree(response);
                        JsonNode languagesNode = json.path("languages");

                        List<LanguageResponse> list = new ArrayList<>();
                        for (JsonNode langNode : languagesNode) {
                            LanguageResponse lang = new LanguageResponse();
                            lang.setLanguage(langNode.path("language").asText());
                            lang.setName(langNode.path("name").asText());
                            list.add(lang);
                        }

                        LanguagesResponse resp = new LanguagesResponse();
                        resp.setLanguages(list);
                        return resp;
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing languages response", e);
                    }
                });
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
