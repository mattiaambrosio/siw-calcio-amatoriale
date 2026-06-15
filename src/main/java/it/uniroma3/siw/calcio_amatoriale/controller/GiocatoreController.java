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

import it.uniroma3.siw.calcio_amatoriale.model.Giocatore;
import it.uniroma3.siw.calcio_amatoriale.service.GiocatoreService;
import it.uniroma3.siw.calcio_amatoriale.service.SquadraService;

@Controller
public class GiocatoreController {

    @Autowired
    private GiocatoreService giocatoreService;

    @Autowired
    private SquadraService squadraService;

    // GET: form nuova giocatore con la lista delle squadre
    @GetMapping("/admin/giocatore/new")
    public String formNewGiocatore(Model model) {
        model.addAttribute("giocatore", new Giocatore());
        model.addAttribute("squadre", squadraService.findAll());
        return "admin/formNewGiocatore";
    }

    // POST: salva il giocatore; controlla duplicati
    @PostMapping("/admin/giocatore")
    public String newGiocatore(@Valid @ModelAttribute("giocatore") Giocatore giocatore, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("squadre", squadraService.findAll());
            return "admin/formNewGiocatore";
        }
        if (!giocatoreService.alreadyExists(giocatore)) {
            giocatoreService.save(giocatore);
            model.addAttribute("messaggioSuccesso", "Giocatore aggiunto alla squadra con successo!");
            return "redirect:/dashboard";
        } else {
            model.addAttribute("messaggioErrore", "Attenzione: Questo giocatore esiste già!");
            model.addAttribute("squadre", squadraService.findAll());
            return "admin/formNewGiocatore";
        }
    }

    // GET: lista di tutti i giocatori (pubblica)
    @GetMapping("/giocatori")
    public String showGiocatori(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("giocatori", giocatoreService.cerca(search));
        return "giocatori";
    }

    // GET: form modifica giocatore (Admin)
    @GetMapping("/admin/giocatore/{id}/edit")
    public String formEditGiocatore(@PathVariable("id") Long id, Model model) {
        Giocatore giocatore = giocatoreService.findById(id);
        if (giocatore == null) return "redirect:/giocatori";
        model.addAttribute("giocatore", giocatore);
        model.addAttribute("squadre", squadraService.findAll());
        return "admin/formEditGiocatore";
    }

    // POST: salva le modifiche al giocatore (Admin)
    @PostMapping("/admin/giocatore/{id}/edit")
    public String editGiocatore(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("giocatore") Giocatore aggiornato, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("squadre", squadraService.findAll());
            return "admin/formEditGiocatore";
        }
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

    // POST: elimina il giocatore (Admin)
    @PostMapping("/admin/giocatore/{id}/delete")
    public String deleteGiocatore(@PathVariable("id") Long id) {
        giocatoreService.delete(id);
        return "redirect:/giocatori";
    }
}