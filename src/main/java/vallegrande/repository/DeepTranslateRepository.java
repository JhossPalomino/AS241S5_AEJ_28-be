package vallegrande.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import vallegrande.model.DeepTranslate;

@Repository
public interface DeepTranslateRepository extends ReactiveMongoRepository<DeepTranslate, String> {
   
}
