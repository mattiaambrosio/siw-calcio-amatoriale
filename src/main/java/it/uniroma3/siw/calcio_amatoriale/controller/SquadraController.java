package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.calcio_amatoriale.model.Squadra;
import it.uniroma3.siw.calcio_amatoriale.service.SquadraService;

@Controller
public class SquadraController {

    @Autowired
    private SquadraService squadraService;

    // GET: form per creare una nuova squadra
    @GetMapping("/admin/squadra/new")
    public String formNewSquadra(Model model) {
        model.addAttribute("squadra", new Squadra());
        return "admin/formNewSquadra";
    }

    // POST: salva la nuova squadra; controlla duplicati per nome
    @PostMapping("/admin/squadra")
    public String newSquadra(@ModelAttribute("squadra") Squadra squadra, Model model) {
        if (!squadraService.alreadyExists(squadra)) {
            squadraService.save(squadra);
            model.addAttribute("messaggioSuccesso", "Squadra salvata con successo nel database!");
            return "redirect:/dashboard";
        } else {
            model.addAttribute("messaggioErrore", "Attenzione: Esiste già una squadra con questo nome!");
            return "admin/formNewSquadra";
        }
    }

    // GET: lista di tutte le squadre (pubblica)
    @GetMapping("/squadre")
    public String showSquadre(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("squadre", squadraService.cerca(search));
        return "squadre";
    }

    // GET: dettaglio di una squadra con i giocatori (pubblica)
    @GetMapping("/squadra/{id}")
    public String showSquadraDetail(@PathVariable("id") Long id, Model model) {
        it.uniroma3.siw.calcio_amatoriale.model.Squadra squadra = squadraService.findById(id);
        if (squadra == null) return "redirect:/squadre";
        model.addAttribute("squadra", squadra);
        return "squadraDetail";
    }

    // GET: form modifica squadra (Admin)
    @GetMapping("/admin/squadra/{id}/edit")
    public String formEditSquadra(@PathVariable("id") Long id, Model model) {
        it.uniroma3.siw.calcio_amatoriale.model.Squadra squadra = squadraService.findById(id);
        if (squadra == null) return "redirect:/squadre";
        model.addAttribute("squadra", squadra);
        return "admin/formEditSquadra";
    }

    // POST: salva le modifiche alla squadra (Admin)
    @PostMapping("/admin/squadra/{id}/edit")
    public String editSquadra(@PathVariable("id") Long id,
                               @ModelAttribute("squadra") it.uniroma3.siw.calcio_amatoriale.model.Squadra aggiornata) {
        it.uniroma3.siw.calcio_amatoriale.model.Squadra squadra = squadraService.findById(id);
        if (squadra == null) return "redirect:/squadre";
        squadra.setNome(aggiornata.getNome());
        squadra.setCitta(aggiornata.getCitta());
        squadra.setAnnoDiFondazione(aggiornata.getAnnoDiFondazione());
        squadraService.save(squadra);
        return "redirect:/squadra/" + id;
    }

    // POST: elimina la squadra (Admin)
    @PostMapping("/admin/squadra/{id}/delete")
    public String deleteSquadra(@PathVariable("id") Long id) {
        squadraService.delete(id);
        return "redirect:/squadre";
    }
}