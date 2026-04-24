package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.calcio_amatoriale.model.Arbitro;
import it.uniroma3.siw.calcio_amatoriale.service.ArbitroService;

@Controller
public class ArbitroController {

    @Autowired
    private ArbitroService arbitroService;

    // Mostra il form di creazione
    @GetMapping("/admin/arbitro/new")
    public String formNewArbitro(Model model) {
        model.addAttribute("arbitro", new Arbitro());
        return "admin/formNewArbitro"; 
    }

    // Salva l'arbitro
    @PostMapping("/admin/arbitro")
    public String newArbitro(@ModelAttribute("arbitro") Arbitro arbitro, Model model) {
        if (!arbitroService.alreadyExists(arbitro)) {
            arbitroService.save(arbitro);
            model.addAttribute("messaggioSuccesso", "Arbitro registrato con successo!");
            return "index";
        } else {
            model.addAttribute("messaggioErrore", "Attenzione: Esiste già un arbitro con questo Codice!");
            return "admin/formNewArbitro";
        }
    }

    // Mostra tutti gli arbitri
    @GetMapping("/arbitri")
    public String showArbitri(Model model) {
        model.addAttribute("arbitri", arbitroService.findAll());
        return "arbitri";
    }
}