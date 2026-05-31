package vallegrande.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rapidapi.youtube")
public class YoutubeApiProperties {
   @SuppressWarnings("unused")
   private String url;
   @SuppressWarnings("unused")
   private String host;
   @SuppressWarnings("unused")
   private String apiKey;    
}
