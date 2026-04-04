package vallegrande.dto;

import java.util.List;

import lombok.Data;

@Data
public class YoutubeDowloaderResponse {
    
    private String videoURL;
    private String title;
    private String downloadUrl;
    private String progress;
    private String status;
    private String createdAt;
    private String updatedAt;
    private List<String> alternativeDownloadUrls;

    
}