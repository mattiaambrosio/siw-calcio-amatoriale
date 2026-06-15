package it.uniroma3.siw.calcio_amatoriale.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.calcio_amatoriale.model.Giocatore;

public interface GiocatoreRepository extends CrudRepository<Giocatore, Long> {

    public boolean existsByNomeAndCognome(String nome, String cognome);

    List<Giocatore> findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(String nome, String cognome);
}