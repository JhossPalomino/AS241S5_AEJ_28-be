package vallegrande.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "youtube")
public class YoutubeDownloader {
    
    @Id
    private String id;
    private String videoURL;
    private String title;
    private String downloadUrl;
    private String createdAt;

}
