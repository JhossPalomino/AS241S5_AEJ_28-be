package vallegrande.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vallegrande.service.YoutubeDownloaderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;
import vallegrande.model.YoutubeDownloader;
import vallegrande.dto.YoutubeDownloadRequest;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/youtube")
public class YoutubeDownloaderRest {
    private final YoutubeDownloaderService service;

    public YoutubeDownloaderRest(YoutubeDownloaderService youtubeDownloaderService) {
        this.service = youtubeDownloaderService;
    }

    @PostMapping("/download")
    public Mono<YoutubeDownloader> processVideo(@RequestBody YoutubeDownloadRequest request) {
        return service.processVideo(request);
    }

    @GetMapping("/all")
    public Flux<YoutubeDownloader> getAllVideos() {
        return service.getAllDownloads();
    }
}
