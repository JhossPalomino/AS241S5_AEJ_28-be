package vallegrande.dto.tts;
import lombok.Data;

@Data
public class TtsRequest {
    private String voice;
    private String text;
}
