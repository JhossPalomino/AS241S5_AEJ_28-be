package vallegrande.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YoutubeDownloadRequest {
    private String format = "mp3";
    private String url;
    
    @JsonProperty("audio_quality")
    private Number audioQuality = 128;

    @JsonProperty("allow_extended_duration")
    private boolean allowExtendedDuration = false;

    @JsonProperty("audio_language")
    private String audioLanguage = "en";

    public String getVideoUrl() {
        return url;
    }

    public void setVideoUrl(String videoUrl) {
        this.url = videoUrl;
    }

}