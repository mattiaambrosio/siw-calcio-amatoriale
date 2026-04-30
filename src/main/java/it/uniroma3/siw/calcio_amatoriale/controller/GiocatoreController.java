package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    // Form modifica giocatore (Admin)
    @GetMapping("/admin/giocatore/{id}/edit")
    public String formEditGiocatore(@PathVariable("id") Long id, Model model) {
        Giocatore giocatore = giocatoreService.findById(id);
        if (giocatore == null) return "redirect:/giocatori";
        model.addAttribute("giocatore", giocatore);
        model.addAttribute("squadre", squadraService.findAll());
        return "admin/formEditGiocatore";
    }

    // Salva modifica giocatore (Admin)
    @PostMapping("/admin/giocatore/{id}/edit")
    public String editGiocatore(@PathVariable("id") Long id,
                                 @ModelAttribute("giocatore") Giocatore aggiornato) {
        Giocatore giocatore = giocatoreService.findById(id);
        if (giocatore == null) return "redirect:/giocatori";
        giocatore.setNome(aggiornato.getNome());
        giocatore.setCognome(aggiornato.getCognome());
        giocatore.setRuolo(aggiornato.getRuolo());
        giocatore.setAltezza(aggiornato.getAltezza());
        giocatore.setDataDiNascita(aggiornato.getDataDiNascita());
        giocatore.setSquadra(aggiornato.getSquadra());
        giocatoreService.save(giocatore);
        return "redirect:/giocatori";
    }

    // Elimina giocatore (Admin)
    @PostMapping("/admin/giocatore/{id}/delete")
    public String deleteGiocatore(@PathVariable("id") Long id) {
        giocatoreService.delete(id);
        return "redirect:/giocatori";
    }
}