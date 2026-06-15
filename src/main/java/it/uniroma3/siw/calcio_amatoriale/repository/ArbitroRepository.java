package it.uniroma3.siw.calcio_amatoriale.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.calcio_amatoriale.model.Arbitro;

public interface ArbitroRepository extends CrudRepository<Arbitro, Long> {

    public boolean existsByCodiceArbitrale(String codiceArbitrale);

    List<Arbitro> findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(String nome, String cognome);
}