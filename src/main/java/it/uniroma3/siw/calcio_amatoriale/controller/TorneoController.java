package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import it.uniroma3.siw.calcio_amatoriale.model.SquadraPunteggio;
import it.uniroma3.siw.calcio_amatoriale.model.Partita;
import it.uniroma3.siw.calcio_amatoriale.model.Squadra;
import it.uniroma3.siw.calcio_amatoriale.model.Torneo;
import it.uniroma3.siw.calcio_amatoriale.service.TorneoService;

@Controller
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    @Autowired
    private it.uniroma3.siw.calcio_amatoriale.service.SquadraService squadraService;

    // GET: form per creare un nuovo torneo
    @GetMapping("/admin/torneo/new")
    public String formNewTorneo(Model model) {
        model.addAttribute("torneo", new Torneo());
        return "admin/formNewTorneo";
    }

    // POST: salva il nuovo torneo; controlla duplicati per nome
    @PostMapping("/admin/torneo")
    public String newTorneo(@Valid @ModelAttribute("torneo") Torneo torneo, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin/formNewTorneo";
        }
        if (!torneoService.alreadyExists(torneo)) {
            torneoService.save(torneo);
            model.addAttribute("messaggioSuccesso", "Torneo salvato con successo nel database!");
            return "redirect:/dashboard";
        } else {
            model.addAttribute("messaggioErrore", "Attenzione: Esiste già un torneo con questo nome!");
            return "admin/formNewTorneo";
        }
    }

    // GET: form di modifica torneo precompilato con i dati esistenti
    @GetMapping("/admin/torneo/{id}/edit")
    public String formEditTorneo(@PathVariable("id") Long id, Model model) {
        Torneo torneo = torneoService.findById(id);
        if (torneo == null) return "redirect:/tornei";
        model.addAttribute("torneo", torneo);
        return "admin/formEditTorneo";
    }

    // POST: salva le modifiche al torneo
    @PostMapping("/admin/torneo/{id}/edit")
    public String editTorneo(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("torneo") Torneo torneoAggiornato, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Serve l'id nel model per ricostruire il form (action URL)
            torneoAggiornato.setId(id);
            return "admin/formEditTorneo";
        }
        Torneo torneo = torneoService.findById(id);
        if (torneo == null) return "redirect:/tornei";
        torneo.setNome(torneoAggiornato.getNome());
        torneo.setAnno(torneoAggiornato.getAnno());
        torneo.setDescrizione(torneoAggiornato.getDescrizione());
        torneoService.save(torneo);
        return "redirect:/torneo/" + id;
    }

    // POST: elimina il torneo
    @PostMapping("/admin/torneo/{id}/delete")
    public String deleteTorneo(@PathVariable("id") Long id) {
        torneoService.delete(id);
        return "redirect:/tornei";
    }

    // GET: lista di tutti i tornei (pubblica)
    @GetMapping("/tornei")
    public String showTornei(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("tornei", torneoService.cerca(search));
        return "tornei";
    }

    // GET: dettaglio di un singolo torneo (pubblica)
    @GetMapping("/torneo/{id}")
    public String showTorneoDetail(@PathVariable("id") Long id, Model model) {
        Torneo torneo = torneoService.findById(id);
        if (torneo == null) {
            return "redirect:/tornei";
        }
        model.addAttribute("torneo", torneo);
        return "torneoDetail";
    }

    // GET: form iscrizione squadra a torneo
    @GetMapping("/admin/iscrizione")
    public String formIscrizione(Model model) {
        model.addAttribute("tornei", torneoService.findAll());
        model.addAttribute("squadre", squadraService.findAll());
        return "admin/formIscrizione";
    }

    // POST: salva l'iscrizione di una squadra al torneo
    @PostMapping("/admin/iscrizione")
    public String iscriviSquadra(@org.springframework.web.bind.annotation.RequestParam("torneoId") Long torneoId,
            @org.springframework.web.bind.annotation.RequestParam("squadraId") Long squadraId,
            Model model) {

        String esito = torneoService.iscriviSquadraATorneo(torneoId, squadraId);

        switch (esito) {
            case "OK":
                model.addAttribute("messaggioSuccesso", "Squadra iscritta al torneo con successo!");
                return "redirect:/dashboard";
            case "GIA_ISCRITTA":
                model.addAttribute("messaggioErrore", "Attenzione: La squadra è già iscritta a questo torneo!");
                model.addAttribute("tornei", torneoService.findAll());
                model.addAttribute("squadre", squadraService.findAll());
                return "admin/formIscrizione";
            default:
                model.addAttribute("messaggioErrore", "Errore: Torneo o squadra non trovati.");
                model.addAttribute("tornei", torneoService.findAll());
                model.addAttribute("squadre", squadraService.findAll());
                return "admin/formIscrizione";
        }
    }

    @GetMapping("/torneo/{id}/classifica")
    public String showClassifica(@PathVariable("id") Long id, Model model) {
        Torneo torneo = torneoService.findById(id);
        if (torneo == null) {
            return "redirect:/tornei";
        }

        model.addAttribute("torneo", torneo);
        model.addAttribute("classifica", torneoService.calcolaClassifica(id));
        return "classifica";
    }
}