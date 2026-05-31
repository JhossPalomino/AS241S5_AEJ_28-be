package vallegrande.service.impl;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.service.YoutubeDownloaderService;
import vallegrande.dto.YoutubeDownloadRequest;
import vallegrande.exception.YoutubeApiProcessingException;
import vallegrande.model.YoutubeDownloader;
import vallegrande.repository.YoutubeDownloaderRepository;

@Service
public class YoutubeDownloaderServiceImpl implements YoutubeDownloaderService {

    private final WebClient youtubeWebClient;
    private final YoutubeDownloaderRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public YoutubeDownloaderServiceImpl(WebClient youtubeWebClient, YoutubeDownloaderRepository repository) {
        this.youtubeWebClient = youtubeWebClient;
        this.repository = repository;
    }

    @Override
    public Mono<YoutubeDownloader> processVideo(YoutubeDownloadRequest request) {
        return youtubeWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ajax/download.php")
                        .queryParam("format", request.getFormat())
                        .queryParam("url", request.getVideoUrl())
                        .queryParam("audio_quality", request.getAudioQuality())
                        .queryParam("allow_extended_duration", request.isAllowExtendedDuration())
                        .queryParam("audio_language", request.getAudioLanguage())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode json = mapper.readTree(response);

                        String title = json.path("title").asText();
                        if (title == null || title.isEmpty()) {
                            title = json.path("info").path("title").asText();
                        }

                        String progressUrl = json.path("progress_url").asText();

                        YoutubeDownloader downloader = new YoutubeDownloader();
                        downloader.setVideoURL(request.getVideoUrl());
                        downloader.setTitle(title);
                        downloader.setDownloadUrl(progressUrl); // inicial
                        downloader.setCreatedAt(Instant.now().toString());
                        return downloader;
                    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                        throw new YoutubeApiProcessingException("Error parsing JSON", e);
                    }
                })
                .flatMap(repository::save)
                .flatMap(this::pollProgress); 
    }

    private Mono<YoutubeDownloader> pollProgress(YoutubeDownloader downloader) {
        return Flux.interval(Duration.ofSeconds(2)) 
                .flatMap(i -> youtubeWebClient.get()
                        .uri(downloader.getDownloadUrl()) 
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(resp -> {
                            try {
                                JsonNode progressJson = mapper.readTree(resp);
                                int progress = progressJson.path("progress").asInt();

                                if (progress == 1000) {
                                    downloader.setDownloadUrl(progressJson.path("download_url").asText());
                                }
                                return downloader;
                            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                                throw new YoutubeApiProcessingException("Error parsing JSON", e);
                            }
                        }))
                .filter(d -> !d.getDownloadUrl().contains("progress?id="))
                .next()
                .flatMap(repository::save);
    }

    @Override
    public Flux<YoutubeDownloader> getAllDownloads() {
        return repository.findAll();
    }
}
