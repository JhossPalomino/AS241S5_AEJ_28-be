package vallegrande.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "tts")
public class Tts {
    @Id
    private String id;
    private String text;
    private String voice;
    private String audioFileId;
    private LocalDateTime createdAt;
    private Boolean status;
}
