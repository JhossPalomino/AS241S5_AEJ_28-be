package vallegrande.dto.translate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguagesResponse{
    private List<LanguageResponse> languages;
}
