package it.uniroma3.siw.calcio_amatoriale.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.calcio_amatoriale.model.Credentials;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {
    
    // Metodo fondamentale per cercare l'utente durante il login
    public Optional<Credentials> findByUsername(String username);
}