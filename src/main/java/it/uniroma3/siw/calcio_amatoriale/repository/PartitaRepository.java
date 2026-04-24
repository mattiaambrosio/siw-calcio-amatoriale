package it.uniroma3.siw.calcio_amatoriale.repository;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.calcio_amatoriale.model.Partita;

public interface PartitaRepository extends CrudRepository<Partita, Long> {
}