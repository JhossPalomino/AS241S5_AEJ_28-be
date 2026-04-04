package vallegrande.dto.translate;

import lombok.Data;

@Data
public class TranslateRequest {
    private String text;
    private String sourceLang;
    private String targetLang;

}
