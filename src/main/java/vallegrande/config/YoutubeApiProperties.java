package vallegrande.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rapidapi.youtube")
public class YoutubeApiProperties {
   private String url;
   private String host;
   private String apiKey;    
}
