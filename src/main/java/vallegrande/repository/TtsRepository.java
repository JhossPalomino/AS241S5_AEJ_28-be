package vallegrande.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import vallegrande.model.Tts;

public interface TtsRepository extends ReactiveMongoRepository<Tts, String> {
    
}
