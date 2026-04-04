package vallegrande.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import vallegrande.model.YoutubeDownloader;
import org.springframework.stereotype.Repository;

@Repository
public interface YoutubeDownloaderRepository extends ReactiveMongoRepository<YoutubeDownloader, String> {
    
}
