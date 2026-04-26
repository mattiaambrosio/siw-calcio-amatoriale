package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Questo punta a templates/login.html
    }
}