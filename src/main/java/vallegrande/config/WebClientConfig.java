package vallegrande.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {
    @Value("${rapidapi.youtube.url}")
    private String youtubeUrl;
    @Value("${rapidapi.youtube.host}")
    private String youtubeHost;
    @Value("${rapidapi.youtube.apikey}")
    private String youtubeApiKey;

    @Value("${rapidapi.translate.url}")
    private String translateUrl;
    @Value("${rapidapi.translate.host}")
    private String translateHost;
    @Value("${rapidapi.translate.apikey}")
    private String translateApiKey;

    @Value("${rapidapi.tts.url}")
    private String ttsUrl;
    @Value("${rapidapi.tts.host}")
    private String ttsHost;
    @Value("${rapidapi.tts.apikey}")
    private String ttsApiKey;

    @SuppressWarnings("null")
    @Bean
    public WebClient youtubeWebClient() {
        return WebClient.builder()
                .baseUrl(youtubeUrl)
                .defaultHeader("X-RapidAPI-Host", youtubeHost)
                .defaultHeader("X-RapidAPI-Key", youtubeApiKey)
                .build();
    }

    @SuppressWarnings("null")
    @Bean
    public WebClient translateWebClient() {
        return WebClient.builder()
                .baseUrl(translateUrl)
                .defaultHeader("X-RapidAPI-Host", translateHost)
                .defaultHeader("X-RapidAPI-Key", translateApiKey)
                .build();
    }

    @SuppressWarnings("null")
    @Bean
    public WebClient ttsWebClient() {
        return WebClient.builder()
                .baseUrl(ttsUrl)
                .defaultHeader("X-RapidAPI-Host", ttsHost)
                .defaultHeader("X-RapidAPI-Key", ttsApiKey)
                .build();
    }
}
