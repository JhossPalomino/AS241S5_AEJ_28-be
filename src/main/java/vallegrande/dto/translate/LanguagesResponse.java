package vallegrande.dto.translate;
import java.util.List;

import lombok.Data;


@Data
public class LanguagesResponse{
    private List<LanguageResponse> languages;
}
