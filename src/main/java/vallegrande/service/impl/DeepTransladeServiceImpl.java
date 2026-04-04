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
                        String translatedText = json.path("data")
                                .path("translations")
                                .path("translatedText")
                                .get(0)
                                .asText();

                        DeepTranslate translation = new DeepTranslate();
                        translation.setSourceLanguage(sourceLang);
                        translation.setTargetLanguage(targetLang);
                        translation.setOriginalText(text);
                        translation.setTranslatedText(translatedText);
                        translation.setCreatedAt(Instant.now());

                        return translation; // aquí devuelves el objeto, no un Mono
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing translation response", e);
                    }
                })
                .flatMap(repository::save); // aquí sí persistes de manera reactiva
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

}
