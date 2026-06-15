package it.uniroma3.siw.calcio_amatoriale.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.calcio_amatoriale.model.Commento;
import it.uniroma3.siw.calcio_amatoriale.model.CommentoDTO;
import it.uniroma3.siw.calcio_amatoriale.model.Credentials;
import it.uniroma3.siw.calcio_amatoriale.model.Partita;
import it.uniroma3.siw.calcio_amatoriale.repository.CredentialsRepository;
import it.uniroma3.siw.calcio_amatoriale.service.CommentoService;
import it.uniroma3.siw.calcio_amatoriale.service.PartitaService;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * REST Controller per i commenti — usato da React per operazioni asincrone.
 * Restituisce JSON invece di template Thymeleaf.
 */
@RestController
@RequestMapping("/api")
public class CommentoRestController {

    @Autowired
    private CommentoService commentoService;
    @Autowired
    private PartitaService partitaService;
    @Autowired
    private CredentialsRepository credentialsRepository;

    // ─────────────────────────────────────────────────────────────────
    // GET /api/partita/{id}/commenti — pubblica, visibile a tutti
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/partita/{id}/commenti")
    public ResponseEntity<List<CommentoDTO>> getCommenti(@PathVariable("id") Long partitaId) {
        List<CommentoDTO> dtos = commentoService.findByPartita(partitaId)
                .stream()
                .map(CommentoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ─────────────────────────────────────────────────────────────────
    // POST /api/partita/{id}/commento — richiede autenticazione
    // ─────────────────────────────────────────────────────────────────
    @PostMapping("/partita/{id}/commento")
    public ResponseEntity<?> addCommento(@PathVariable("id") Long partitaId,
            @RequestBody Map<String, String> body,
            Authentication principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("errore", "Non autenticato"));
        }

        String testo = body.get("testo");
        if (testo == null || testo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("errore", "Il testo non può essere vuoto"));
        }

        Partita partita = partitaService.findById(partitaId);
        Credentials autore = credentialsRepository.findByEmail(estraiEmail(principal)).orElse(null);

        if (partita == null || autore == null) {
            return ResponseEntity.notFound().build();
        }

        Commento commento = new Commento();
        commento.setTesto(testo.trim());
        commento.setDataCreazione(LocalDateTime.now());
        commento.setPartita(partita);
        commento.setAutore(autore);
        commentoService.save(commento);

        return ResponseEntity.ok(new CommentoDTO(commento));
    }

    // ─────────────────────────────────────────────────────────────────
    // PUT /api/commento/{id} — solo il proprietario
    // ─────────────────────────────────────────────────────────────────
    @PutMapping("/commento/{id}")
    public ResponseEntity<?> editCommento(@PathVariable("id") Long id,
            @RequestBody Map<String, String> body,
            Authentication principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("errore", "Non autenticato"));
        }

        Commento commento = commentoService.findById(id);
        if (commento == null)
            return ResponseEntity.notFound().build();

        if (!commentoService.isOwner(id, estraiEmail(principal))) {
            return ResponseEntity.status(403).body(Map.of("errore", "Non autorizzato"));
        }

        String testo = body.get("testo");
        if (testo == null || testo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("errore", "Il testo non può essere vuoto"));
        }

        commento.setTesto(testo.trim());
        commentoService.save(commento);
        return ResponseEntity.ok(new CommentoDTO(commento));
    }

    // ─────────────────────────────────────────────────────────────────
    // DELETE /api/commento/{id} — solo il proprietario
    // ─────────────────────────────────────────────────────────────────
    @DeleteMapping("/commento/{id}")
    public ResponseEntity<?> deleteCommento(@PathVariable("id") Long id, Authentication principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("errore", "Non autenticato"));
        }

        Commento commento = commentoService.findById(id);
        if (commento == null)
            return ResponseEntity.notFound().build();

        boolean eliminato = commentoService.deleteIfOwner(id, estraiEmail(principal));
        if (!eliminato) {
            return ResponseEntity.status(403).body(Map.of("errore", "Non autorizzato"));
        }

        return ResponseEntity.ok(Map.of("messaggio", "Commento eliminato"));
    }

    // ─────────────────────────────────────────────────────────────────
    // GET /api/me — chi sono? (usato da React per sapere l'utente loggato)
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication principal) {
        if (principal == null) {
            return ResponseEntity.ok(Map.of("autenticato", false));
        }
        return ResponseEntity.ok(Map.of(
                "autenticato", true,
                "email", estraiEmail(principal)));
    }

    private String estraiEmail(Authentication auth) {
        if (auth == null)
            return null;
        if (auth.getPrincipal() instanceof OAuth2User oauthUser) {
            return oauthUser.getAttribute("email");
        }
        return auth.getName();
    }
}
