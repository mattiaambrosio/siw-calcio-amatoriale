package it.uniroma3.siw.calcio_amatoriale.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.calcio_amatoriale.model.Commento;
import it.uniroma3.siw.calcio_amatoriale.model.Credentials;
import it.uniroma3.siw.calcio_amatoriale.model.Partita;
import it.uniroma3.siw.calcio_amatoriale.repository.CredentialsRepository;
import it.uniroma3.siw.calcio_amatoriale.service.CommentoService;
import it.uniroma3.siw.calcio_amatoriale.service.PartitaService;

@Controller
public class CommentoController {

    @Autowired private CommentoService commentoService;
    @Autowired private PartitaService partitaService;
    @Autowired private CredentialsRepository credentialsRepository;

    // ─────────────────────────────────────────────────────────────────
    // Dettaglio partita + lista commenti (accessibile a tutti gli autenticati)
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/partita/{id}")
    public String showPartitaDetail(@PathVariable("id") Long id, Model model, Principal principal) {
        Partita partita = partitaService.findById(id);
        if (partita == null) return "redirect:/partite";

        model.addAttribute("partita", partita);
        model.addAttribute("commenti", commentoService.findByPartita(id));
        model.addAttribute("nuovoCommento", new Commento());

        // Passiamo l'email dell'utente loggato al template per controllo ownership
        if (principal != null) {
            model.addAttribute("emailLoggato", principal.getName());
        }
        return "partitaDetail";
    }

    // ─────────────────────────────────────────────────────────────────
    // Inserimento nuovo commento (solo utenti autenticati)
    // ─────────────────────────────────────────────────────────────────
    @PostMapping("/partita/{id}/commento")
    public String addCommento(@PathVariable("id") Long id,
                               @RequestParam("testo") String testo,
                               Principal principal) {
        if (testo == null || testo.isBlank()) {
            return "redirect:/partita/" + id;
        }

        Partita partita = partitaService.findById(id);
        Credentials autore = credentialsRepository.findByEmail(principal.getName()).orElse(null);

        if (partita == null || autore == null) return "redirect:/partite";

        Commento commento = new Commento();
        commento.setTesto(testo.trim());
        commento.setDataCreazione(LocalDateTime.now());
        commento.setPartita(partita);
        commento.setAutore(autore);

        commentoService.save(commento);
        return "redirect:/partita/" + id;
    }

    // ─────────────────────────────────────────────────────────────────
    // Form modifica commento (solo il proprietario)
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/commento/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, Principal principal) {
        Commento commento = commentoService.findById(id);
        if (commento == null) return "redirect:/partite";

        // Solo il proprietario può modificare
        if (!commentoService.isOwner(id, principal.getName())) {
            return "redirect:/partita/" + commento.getPartita().getId();
        }

        model.addAttribute("commento", commento);
        return "editCommento";
    }

    // ─────────────────────────────────────────────────────────────────
    // Salvataggio modifica commento (solo il proprietario)
    // ─────────────────────────────────────────────────────────────────
    @PostMapping("/commento/{id}/edit")
    public String saveEditCommento(@PathVariable("id") Long id,
                                    @RequestParam("testo") String testo,
                                    Principal principal) {
        Commento commento = commentoService.findById(id);
        if (commento == null) return "redirect:/partite";

        // Sicurezza: solo il proprietario può salvare
        if (!commentoService.isOwner(id, principal.getName())) {
            return "redirect:/partita/" + commento.getPartita().getId();
        }

        if (testo != null && !testo.isBlank()) {
            commento.setTesto(testo.trim());
            commentoService.save(commento);
        }

        return "redirect:/partita/" + commento.getPartita().getId();
    }

    // ─────────────────────────────────────────────────────────────────
    // Eliminazione commento (solo il proprietario)
    // ─────────────────────────────────────────────────────────────────
    @PostMapping("/commento/{id}/delete")
    public String deleteCommento(@PathVariable("id") Long id, Principal principal) {
        Commento commento = commentoService.findById(id);
        if (commento == null) return "redirect:/partite";

        Long partitaId = commento.getPartita().getId();
        commentoService.deleteIfOwner(id, principal.getName());
        return "redirect:/partita/" + partitaId;
    }
}
