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

    // GET: form nuova partita con le opzioni per i menu a tendina
    @GetMapping("/admin/partita/new")
    public String formNewPartita(Model model) {
        model.addAttribute("partita", new Partita());
        model.addAttribute("tornei", torneoService.findAll());
        model.addAttribute("squadre", squadraService.findAll());
        model.addAttribute("arbitri", arbitroService.findAll());
        return "admin/formNewPartita";
    }

    // POST: salva la nuova partita; controlla che le due squadre siano diverse
    @PostMapping("/admin/partita")
    public String newPartita(@ModelAttribute("partita") Partita partita, Model model) {
        
        // Controllo: la squadra casa e ospite devono essere diverse
        if (partita.getSquadraCasa().getId().equals(partita.getSquadraOspite().getId())) {
            model.addAttribute("messaggioErrore", "Errore: La squadra in casa e quella in trasferta devono essere diverse!");
            // Ricarica le liste in caso di errore
            model.addAttribute("tornei", torneoService.findAll());
            model.addAttribute("squadre", squadraService.findAll());
            model.addAttribute("arbitri", arbitroService.findAll());
            return "admin/formNewPartita";
        }

        // Stato iniziale delle nuove partite
        partita.setStato("PROGRAMMATA");
        
        partitaService.save(partita);
        model.addAttribute("messaggioSuccesso", "Partita programmata con successo!");
        return "redirect:/dashboard";
    }

    // GET: lista di tutte le partite (pubblica)
    @GetMapping("/partite")
    public String showPartite(Model model) {
        model.addAttribute("partite", partitaService.findAll());
        return "partite";
    }

    // GET: lista partite per l'admin (selezione partita da aggiornare)
    @GetMapping("/admin/partite/punteggio")
    public String sceltaPartitaPunteggio(Model model) {
        model.addAttribute("partite", partitaService.findAll());
        return "admin/sceltaPartitaPunteggio";
    }

    // GET: form per inserire il risultato di una partita
    @GetMapping("/admin/partita/risultato/{id}")
    public String formRisultato(@PathVariable("id") Long id, Model model) {
        Partita partita = partitaService.findById(id);
        model.addAttribute("partita", partita);
        return "admin/formRisultato";
    }

    // POST: salva il risultato della partita e cambia stato in GIOCATA
    @PostMapping("/admin/partita/risultato/{id}")
    public String saveRisultato(@PathVariable("id") Long id, 
                                @RequestParam("goalsHome") Integer goalsHome,
                                @RequestParam("goalsAway") Integer goalsAway) {
        Partita partita = partitaService.findById(id);
        
        partita.setGoalsHome(goalsHome);
        partita.setGoalsAway(goalsAway);
        partita.setStato("GIOCATA");
        
        partitaService.save(partita);
        return "redirect:/partite";
    }

    // POST: elimina la partita
    @PostMapping("/admin/partita/{id}/delete")
    public String deletePartita(@PathVariable("id") Long id) {
        partitaService.delete(id);
        return "redirect:/partite";
    }
}