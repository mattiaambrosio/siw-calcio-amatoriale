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
            return "index"; // Torna alla home dopo aver salvato
        } else {
            model.addAttribute("messaggioErrore", "Attenzione: Esiste già un torneo con questo nome!");
            return "admin/formNewTorneo"; // Rimane sulla pagina per farti correggere
        }
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

    // Salva l'iscrizione
    @PostMapping("/admin/iscrizione")
    @org.springframework.transaction.annotation.Transactional
    public String iscriviSquadra(@org.springframework.web.bind.annotation.RequestParam("torneoId") Long torneoId,
            @org.springframework.web.bind.annotation.RequestParam("squadraId") Long squadraId,
            Model model) {

        Torneo torneo = torneoService.findById(torneoId);
        it.uniroma3.siw.calcio_amatoriale.model.Squadra squadra = squadraService.findById(squadraId);

        // Evitiamo crash se la lista è vuota
        if (squadra.getTornei() == null) {
            squadra.setTornei(new java.util.ArrayList<>());
        }

        // Controlliamo che la squadra non sia già iscritta a questo torneo
        // Controlliamo che la squadra non sia già iscritta a questo torneo
        if (!squadra.getTornei().contains(torneo)) {
            squadra.getTornei().add(torneo);
            torneo.getSquadre().add(squadra); // <-- AGGIUNGI QUESTA RIGA: Sincronizza anche l'altro lato!

            squadraService.save(squadra);
            model.addAttribute("messaggioSuccesso", "Squadra iscritta al torneo con successo!");
            return "index";

        } else {
            model.addAttribute("messaggioErrore", "Attenzione: La squadra è già iscritta a questo torneo!");
            model.addAttribute("tornei", torneoService.findAll());
            model.addAttribute("squadre", squadraService.findAll());
            return "admin/formIscrizione";
        }
    }

    @GetMapping("/torneo/{id}/classifica")
    public String showClassifica(@PathVariable("id") Long id, Model model) {
        Torneo torneo = torneoService.findById(id);

        // Mappa per tenere traccia dei punteggi di ogni squadra iscritta
        java.util.Map<Long, SquadraPunteggio> classificaMap = new java.util.HashMap<>();
        for (Squadra s : torneo.getSquadre()) {
            classificaMap.put(s.getId(), new SquadraPunteggio(s));
        }

        // Analizziamo le partite del torneo
        if (torneo.getPartite() != null) {
            for (Partita p : torneo.getPartite()) {
                if ("GIOCATA".equals(p.getStato())) {
                    classificaMap.get(p.getSquadraCasa().getId()).aggiungiPartita(p.getGoalsHome(), p.getGoalsAway());
                    classificaMap.get(p.getSquadraOspite().getId()).aggiungiPartita(p.getGoalsAway(), p.getGoalsHome());
                }
            }
        }

        // Trasformiamo la mappa in una lista e ordiniamola
        java.util.List<SquadraPunteggio> classificaOrdinata = new java.util.ArrayList<>(classificaMap.values());
        java.util.Collections.sort(classificaOrdinata);

        model.addAttribute("torneo", torneo);
        model.addAttribute("classifica", classificaOrdinata);
        return "classifica";
    }
}