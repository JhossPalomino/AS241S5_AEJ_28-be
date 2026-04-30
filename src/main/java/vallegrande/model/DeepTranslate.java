package vallegrande.model;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.time.Instant;

import org.springframework.data.annotation.Id;

@Data
@Document(collection = "translate")
public class DeepTranslate {
    
    @Id
    private String id;
    private String sourceLanguage;
    private String targetLanguage;
    private String originalText;
    private String translatedText;
    private Instant createdAt;
    private Boolean status;
    
}
