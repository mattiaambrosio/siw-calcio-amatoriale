package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.calcio_amatoriale.model.Partita;
import it.uniroma3.siw.calcio_amatoriale.service.PartitaService;
import it.uniroma3.siw.calcio_amatoriale.service.TorneoService;
import it.uniroma3.siw.calcio_amatoriale.service.SquadraService;
import it.uniroma3.siw.calcio_amatoriale.service.ArbitroService;

@Controller
public class PartitaController {

    @Autowired private PartitaService partitaService;
    @Autowired private TorneoService torneoService;
    @Autowired private SquadraService squadraService;
    @Autowired private ArbitroService arbitroService;

    // Mostra il form e carica tutte le opzioni per i menu a tendina
    @GetMapping("/admin/partita/new")
    public String formNewPartita(Model model) {
        model.addAttribute("partita", new Partita());
        model.addAttribute("tornei", torneoService.findAll());
        model.addAttribute("squadre", squadraService.findAll());
        model.addAttribute("arbitri", arbitroService.findAll());
        return "admin/formNewPartita"; 
    }

    // Salva la partita
    @PostMapping("/admin/partita")
    public String newPartita(@ModelAttribute("partita") Partita partita, Model model) {
        
        // Controllo logico: una squadra non può giocare contro se stessa!
        if (partita.getSquadraCasa().getId().equals(partita.getSquadraOspite().getId())) {
            model.addAttribute("messaggioErrore", "Errore: La squadra in casa e quella in trasferta devono essere diverse!");
            // Ricarichiamo le liste se c'è un errore, altrimenti la pagina si rompe
            model.addAttribute("tornei", torneoService.findAll());
            model.addAttribute("squadre", squadraService.findAll());
            model.addAttribute("arbitri", arbitroService.findAll());
            return "admin/formNewPartita";
        }

        // Impostiamo di default lo stato "PROGRAMMATA" se stiamo creando una nuova partita
        partita.setStato("PROGRAMMATA");
        
        partitaService.save(partita);
        model.addAttribute("messaggioSuccesso", "Partita programmata con successo!");
        return "index";
    }

    // Mostra tutte le partite
    @GetMapping("/partite")
    public String showPartite(Model model) {
        model.addAttribute("partite", partitaService.findAll());
        return "partite";
    }

    // 1. Lista delle partite ancora da giocare (per l'admin)
    @GetMapping("/admin/partite/punteggio")
    public String sceltaPartitaPunteggio(Model model) {
        // Prendiamo tutte le partite, ma potremmo filtrarle in futuro
        model.addAttribute("partite", partitaService.findAll());
        return "admin/sceltaPartitaPunteggio";
    }

    // 2. Form per inserire i gol di una specifica partita
    @GetMapping("/admin/partita/risultato/{id}")
    public String formRisultato(@PathVariable("id") Long id, Model model) {
        Partita partita = partitaService.findById(id);
        model.addAttribute("partita", partita);
        return "admin/formRisultato";
    }

    // 3. Salvataggio del risultato
    @PostMapping("/admin/partita/risultato/{id}")
    public String saveRisultato(@PathVariable("id") Long id, 
                                @RequestParam("goalsHome") Integer goalsHome,
                                @RequestParam("goalsAway") Integer goalsAway) {
        Partita partita = partitaService.findById(id);
        
        // Aggiorniamo i dati
        partita.setGoalsHome(goalsHome);
        partita.setGoalsAway(goalsAway);
        partita.setStato("GIOCATA"); // Cambiamo lo stato!
        
        partitaService.save(partita);
        return "redirect:/partite"; // Torniamo a vedere il calendario aggiornato
    }
}