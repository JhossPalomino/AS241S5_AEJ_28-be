package vallegrande.service;
import reactor.core.publisher.Mono;
import vallegrande.dto.translate.DetectionResponse;
import vallegrande.dto.translate.LanguagesResponse;
import vallegrande.model.DeepTranslate;

public interface DeepTranslateService {

    Mono<DeepTranslate> translate(String text, String sourceLang, String targetLang);

    Mono<DetectionResponse> detectLanguage(String text);

    Mono<LanguagesResponse> getLanguages();
}
