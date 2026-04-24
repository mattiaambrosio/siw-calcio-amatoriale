package it.uniroma3.siw.calcio_amatoriale.repository;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.calcio_amatoriale.model.Giocatore;

public interface GiocatoreRepository extends CrudRepository<Giocatore, Long> {
    
    public boolean existsByNomeAndCognome(String nome, String cognome);
}