package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.calcio_amatoriale.model.Squadra;
import it.uniroma3.siw.calcio_amatoriale.service.SquadraService;

@Controller
public class SquadraController {

    @Autowired
    private SquadraService squadraService;

    // Rotta per mostrare il form di creazione
    @GetMapping("/admin/squadra/new")
    public String formNewSquadra(Model model) {
        model.addAttribute("squadra", new Squadra());
        return "admin/formNewSquadra"; // Cerca il file formNewSquadra.html in templates/admin
    }

    // Rotta per salvare i dati
    @PostMapping("/admin/squadra")
    public String newSquadra(@ModelAttribute("squadra") Squadra squadra, Model model) {
        if (!squadraService.alreadyExists(squadra)) {
            squadraService.save(squadra);
            model.addAttribute("messaggioSuccesso", "Squadra salvata con successo nel database!");
            return "index";
        } else {
            model.addAttribute("messaggioErrore", "Attenzione: Esiste già una squadra con questo nome!");
            return "admin/formNewSquadra";
        }
    }

    // Rotta pubblica per vedere tutte le squadre
    @GetMapping("/squadre")
    public String showSquadre(Model model) {
        model.addAttribute("squadre", squadraService.findAll());
        return "squadre"; // Cerca il file squadre.html in templates
    }

    // Rotta pubblica per vedere il dettaglio di una squadra (con giocatori)
    @GetMapping("/squadra/{id}")
    public String showSquadraDetail(@PathVariable("id") Long id, Model model) {
        it.uniroma3.siw.calcio_amatoriale.model.Squadra squadra = squadraService.findById(id);
        if (squadra == null) {
            return "redirect:/squadre";
        }
        model.addAttribute("squadra", squadra);
        return "squadraDetail";
    }
}