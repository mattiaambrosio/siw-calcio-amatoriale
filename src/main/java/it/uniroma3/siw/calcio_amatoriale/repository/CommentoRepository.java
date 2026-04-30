package it.uniroma3.siw.calcio_amatoriale.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import it.uniroma3.siw.calcio_amatoriale.model.Commento;

public interface CommentoRepository extends CrudRepository<Commento, Long> {

    // Tutti i commenti di una partita, ordinati dal più recente
    List<Commento> findByPartitaIdOrderByDataCreazioneDesc(Long partitaId);

    // Tutti i commenti scritti da un certo utente (per la sua email)
    @Query("SELECT c FROM Commento c WHERE c.autore.email = :email")
    List<Commento> findByAutoreEmail(@Param("email") String email);

    // Esiste un commento con questo id scritto da questo utente?
    @Query("SELECT COUNT(c) > 0 FROM Commento c WHERE c.id = :id AND c.autore.email = :email")
    boolean isOwner(@Param("id") Long id, @Param("email") String email);
}
