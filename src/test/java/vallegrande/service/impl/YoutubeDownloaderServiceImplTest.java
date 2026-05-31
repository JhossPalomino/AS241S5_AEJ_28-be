package vallegrande.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vallegrande.dto.YoutubeDownloadRequest;
import vallegrande.exception.YoutubeApiProcessingException;
import vallegrande.model.YoutubeDownloader;
import vallegrande.repository.YoutubeDownloaderRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YoutubeDownloaderServiceImplTest {

    @Mock
    private YoutubeDownloaderRepository repository;

    private YoutubeDownloaderServiceImpl service;

    @BeforeEach
    void setup() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(req -> Mono.just(
                        ClientResponse.create(HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body("{}")
                                .build()
                ))
                .build();

        service = new YoutubeDownloaderServiceImpl(webClient, repository);
    }

    @Test
    void testProcessVideoInvalidJsonThrowsException() {
        String invalidResponse = "{ invalid json ";

        WebClient webClient = WebClient.builder()
                .exchangeFunction(req -> Mono.just(
                        ClientResponse.create(HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body(invalidResponse)
                                .build()
                ))
                .build();

        YoutubeDownloaderServiceImpl service = new YoutubeDownloaderServiceImpl(webClient, repository);

        YoutubeDownloadRequest request = new YoutubeDownloadRequest();
        request.setFormat("mp3");
        request.setVideoUrl("http://youtube.com/test");

        StepVerifier.create(service.processVideo(request))
                .expectError(YoutubeApiProcessingException.class)
                .verify();
    }

    @Test
    void testPollProgressCompletesDownload() {
        String progressResponse = "{ \"progress\": 1000, \"download_url\": \"http://download.com/file.mp3\" }";

        WebClient webClient = WebClient.builder()
                .exchangeFunction(req -> Mono.just(
                        ClientResponse.create(HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body(progressResponse)
                                .build()
                ))
                .build();

        YoutubeDownloader downloader = new YoutubeDownloader();
        downloader.setDownloadUrl("/progress?id=123");

        when(repository.save(any())).thenReturn(Mono.just(downloader));

        YoutubeDownloaderServiceImpl service = new YoutubeDownloaderServiceImpl(webClient, repository);

        YoutubeDownloadRequest request = new YoutubeDownloadRequest();
        request.setFormat("mp3");
        request.setVideoUrl("http://youtube.com/test");

        StepVerifier.create(service.processVideo(request))
                .expectNextMatches(d -> d.getDownloadUrl().equals("http://download.com/file.mp3"))
                .verifyComplete();
    }

    @Test
    void testGetAllDownloads() {
        YoutubeDownloader d1 = new YoutubeDownloader(); d1.setTitle("Video1");
        YoutubeDownloader d2 = new YoutubeDownloader(); d2.setTitle("Video2");

        when(repository.findAll()).thenReturn(Flux.just(d1, d2));

        StepVerifier.create(service.getAllDownloads())
                .expectNext(d1)
                .expectNext(d2)
                .verifyComplete();
    }
}
