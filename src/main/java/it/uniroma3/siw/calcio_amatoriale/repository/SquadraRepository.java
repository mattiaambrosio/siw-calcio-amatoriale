package it.uniroma3.siw.calcio_amatoriale.repository;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.calcio_amatoriale.model.Squadra;

public interface SquadraRepository extends CrudRepository<Squadra, Long> {
    
    public boolean existsByNome(String nome);
}