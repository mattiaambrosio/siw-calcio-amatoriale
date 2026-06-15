package it.uniroma3.siw.calcio_amatoriale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.uniroma3.siw.calcio_amatoriale.model.Credentials;
import it.uniroma3.siw.calcio_amatoriale.model.User;
import it.uniroma3.siw.calcio_amatoriale.repository.CredentialsRepository;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData(CredentialsRepository credentialsRepository) {
        return args -> {
            // Crea l'utente admin di default se non esiste
            if (credentialsRepository.findByEmail("admin@email.it").isEmpty()) {
                User user = new User();
                user.setNome("Mattia");
                user.setCognome("Admin");

                Credentials admin = new Credentials();
                admin.setEmail("admin@email.it");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                admin.setUser(user);

                credentialsRepository.save(admin);
                System.out.println(">>> UTENTE ADMIN CREATO: email: admin@email.it, password: admin123");
            }
        };
    }
}