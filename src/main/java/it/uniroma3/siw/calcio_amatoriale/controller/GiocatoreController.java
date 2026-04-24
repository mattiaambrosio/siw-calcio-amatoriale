package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.calcio_amatoriale.model.Giocatore;
import it.uniroma3.siw.calcio_amatoriale.service.GiocatoreService;
import it.uniroma3.siw.calcio_amatoriale.service.SquadraService;

@Controller
public class GiocatoreController {

    @Autowired
    private GiocatoreService giocatoreService;

    @Autowired
    private SquadraService squadraService;

    // Mostra il form e carica la lista delle squadre per il menu a tendina
    @GetMapping("/admin/giocatore/new")
    public String formNewGiocatore(Model model) {
        model.addAttribute("giocatore", new Giocatore());
        model.addAttribute("squadre", squadraService.findAll()); 
        return "admin/formNewGiocatore"; 
    }

    // Salva il giocatore
    @PostMapping("/admin/giocatore")
    public String newGiocatore(@ModelAttribute("giocatore") Giocatore giocatore, Model model) {
        if (!giocatoreService.alreadyExists(giocatore)) {
            giocatoreService.save(giocatore);
            model.addAttribute("messaggioSuccesso", "Giocatore aggiunto alla squadra con successo!");
            return "index";
        } else {
            model.addAttribute("messaggioErrore", "Attenzione: Questo giocatore esiste già!");
            model.addAttribute("squadre", squadraService.findAll()); // Ricarica le squadre in caso di errore
            return "admin/formNewGiocatore";
        }
    }

    // Rotta pubblica per vedere tutti i giocatori
    @GetMapping("/giocatori")
    public String showGiocatori(Model model) {
        model.addAttribute("giocatori", giocatoreService.findAll());
        return "giocatori";
    }
}