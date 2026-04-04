package vallegrande.service;

import vallegrande.model.YoutubeDownloader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.dto.YoutubeDownloadRequest;

public interface YoutubeDownloaderService {

    Mono<YoutubeDownloader> processVideo(YoutubeDownloadRequest request);   
    Flux<YoutubeDownloader> getAllDownloads();

}