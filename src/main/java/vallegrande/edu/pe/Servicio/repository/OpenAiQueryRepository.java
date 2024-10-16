package vallegrande.edu.pe.Servicio.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import vallegrande.edu.pe.Servicio.model.OpenAiQuery;

@Repository
public interface OpenAiQueryRepository extends ReactiveCrudRepository<OpenAiQuery, Long> {
    
}
