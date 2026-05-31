package vallegrande.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
