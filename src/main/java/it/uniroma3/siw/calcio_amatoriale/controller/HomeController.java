package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index"; // Cerca automaticamente il file index.html in templates
    }
}