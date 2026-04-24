package it.uniroma3.siw.calcio_amatoriale.repository;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.calcio_amatoriale.model.Torneo;

public interface TorneoRepository extends CrudRepository<Torneo, Long> {
    
    // Inserendo questa riga, Spring Boot creerà automaticamente per noi 
    // il codice per cercare un torneo conoscendo il suo nome!
    public boolean existsByNome(String nome);
}