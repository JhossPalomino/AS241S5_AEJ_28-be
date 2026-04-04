package vallegrande.dto;
import lombok.Data;

@Data
public class YoutubeDownloadRequest {
    private String format = "mp3";
    private String url;
    private Number audio_quality = 128; 
    private boolean allow_extended_duration = false;
    private String audio_language = "en";
    
    public String getVideoUrl() {
        return url;
    }
    public void setVideoUrl(String videoUrl) {
        this.url = videoUrl;
    }

}