package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

    // Questa rotta mostra la pagina HTML con il modulo vuoto
    @GetMapping("/admin/torneo/new")
    public String formNewTorneo(Model model) {
        model.addAttribute("torneo", new Torneo());
        return "admin/formNewTorneo"; // Cerca il file formNewTorneo.html nella cartella templates/admin
    }

    // Questa rotta "cattura" i dati quando clicchi su 'Salva'
    @PostMapping("/admin/torneo")
    public String newTorneo(@ModelAttribute("torneo") Torneo torneo, Model model) {
        if (!torneoService.alreadyExists(torneo)) {
            torneoService.save(torneo);
            model.addAttribute("messaggioSuccesso", "Torneo salvato con successo nel database!");
            return "redirect:/dashboard";
        } else {
            model.addAttribute("messaggioErrore", "Attenzione: Esiste già un torneo con questo nome!");
            return "admin/formNewTorneo";
        }
    }

    // Mostra il form di modifica torneo (precompilato)
    @GetMapping("/admin/torneo/{id}/edit")
    public String formEditTorneo(@PathVariable("id") Long id, Model model) {
        Torneo torneo = torneoService.findById(id);
        if (torneo == null) return "redirect:/tornei";
        model.addAttribute("torneo", torneo);
        return "admin/formEditTorneo";
    }

    // Salva la modifica del torneo
    @PostMapping("/admin/torneo/{id}/edit")
    public String editTorneo(@PathVariable("id") Long id,
                              @ModelAttribute("torneo") Torneo torneoAggiornato, Model model) {
        Torneo torneo = torneoService.findById(id);
        if (torneo == null) return "redirect:/tornei";
        torneo.setNome(torneoAggiornato.getNome());
        torneo.setAnno(torneoAggiornato.getAnno());
        torneo.setDescrizione(torneoAggiornato.getDescrizione());
        torneoService.save(torneo);
        return "redirect:/torneo/" + id;
    }

    // Elimina il torneo
    @PostMapping("/admin/torneo/{id}/delete")
    public String deleteTorneo(@PathVariable("id") Long id) {
        torneoService.delete(id);
        return "redirect:/tornei";
    }

    // Questa rotta mostra a TUTTI (anche ai Guest) la lista dei tornei
    @GetMapping("/tornei")
    public String showTornei(Model model) {
        model.addAttribute("tornei", torneoService.findAll());
        return "tornei"; // Cerca il file tornei.html
    }

    // Questa rotta mostra il dettaglio di un singolo torneo (pubblica)
    @GetMapping("/torneo/{id}")
    public String showTorneoDetail(@PathVariable("id") Long id, Model model) {
        Torneo torneo = torneoService.findById(id);
        if (torneo == null) {
            return "redirect:/tornei";
        }
        model.addAttribute("torneo", torneo);
        return "torneoDetail";
    }

    // Mostra il form per iscrivere una squadra a un torneo
    @GetMapping("/admin/iscrizione")
    public String formIscrizione(Model model) {
        model.addAttribute("tornei", torneoService.findAll());
        model.addAttribute("squadre", squadraService.findAll());
        return "admin/formIscrizione";
    }

    // Salva l'iscrizione — la logica è nel Service Layer
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