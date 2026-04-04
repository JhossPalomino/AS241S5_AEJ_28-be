package vallegrande.dto.translate;

import lombok.Data;

@Data
public class TranslateResponse {
    private String originalText;
    private String sourceLang;
    private String targetLang;
    private String translatedText;
    private String createdAt;

}
