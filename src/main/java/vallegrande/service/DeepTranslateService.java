package vallegrande.service;
import reactor.core.publisher.Mono;
import vallegrande.dto.translate.DetectionResponse;
import vallegrande.dto.translate.LanguagesResponse;
import vallegrande.model.DeepTranslate;
import reactor.core.publisher.Flux;

public interface DeepTranslateService {

    Mono<DeepTranslate> translate(String text, String sourceLang, String targetLang);

    Mono<DetectionResponse> detectLanguage(String text);

    Mono<LanguagesResponse> getLanguages();

    Flux<DeepTranslate> getAllTranslations();

    Mono<DeepTranslate> getTranslationById(String id);

    Mono<DeepTranslate> updateTranslation(String id, DeepTranslate data);

    Mono<DeepTranslate> deleteTranslation(String id);

    Mono<DeepTranslate> restoreTts(String id);
}
