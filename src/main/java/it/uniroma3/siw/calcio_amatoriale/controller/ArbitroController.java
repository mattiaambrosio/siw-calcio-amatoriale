package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

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
    public String newArbitro(@Valid @ModelAttribute("arbitro") Arbitro arbitro, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin/formNewArbitro";
        }
        if (!arbitroService.alreadyExists(arbitro)) {
            arbitroService.save(arbitro);
            model.addAttribute("messaggioSuccesso", "Arbitro registrato con successo!");
           return "redirect:/dashboard";
        } else {
            model.addAttribute("messaggioErrore", "Attenzione: Esiste già un arbitro con questo Codice!");
            return "admin/formNewArbitro";
        }
    }

    // Mostra tutti gli arbitri
    @GetMapping("/arbitri")
    public String showArbitri(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("arbitri", arbitroService.cerca(search));
        return "arbitri";
    }
}