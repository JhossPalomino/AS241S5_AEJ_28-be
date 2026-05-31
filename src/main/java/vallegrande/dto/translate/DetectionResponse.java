package vallegrande.dto.translate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectionResponse {
    
    private String text;
    private String detectedLang;

}
