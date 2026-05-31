package vallegrande.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {
    private static final String HEADER_RAPIDAPI_HOST = "X-RapidAPI-Host";
    private static final String HEADER_RAPIDAPI_KEY = "X-RapidAPI-Key";

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

    @Bean
    public WebClient youtubeWebClient() {
        return WebClient.builder()
                .baseUrl(youtubeUrl)
                .defaultHeader(HEADER_RAPIDAPI_HOST, youtubeHost)
                .defaultHeader(HEADER_RAPIDAPI_KEY, youtubeApiKey)
                .build();
    }

    @Bean
    public WebClient translateWebClient() {
        return WebClient.builder()
                .baseUrl(translateUrl)
                .defaultHeader(HEADER_RAPIDAPI_HOST, translateHost)
                .defaultHeader(HEADER_RAPIDAPI_KEY, translateApiKey)
                .build();
    }

    @Bean
    public WebClient ttsWebClient() {
        return WebClient.builder()
                .baseUrl(ttsUrl)
                .defaultHeader(HEADER_RAPIDAPI_HOST, ttsHost)
                .defaultHeader(HEADER_RAPIDAPI_KEY, ttsApiKey)
                .build();
    }
}
