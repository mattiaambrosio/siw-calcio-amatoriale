package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.calcio_amatoriale.model.Credentials;
import it.uniroma3.siw.calcio_amatoriale.model.RegistrationForm;
import it.uniroma3.siw.calcio_amatoriale.model.User;
import it.uniroma3.siw.calcio_amatoriale.repository.CredentialsRepository;

@Controller
public class AuthController {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        return "dashboard";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegistrationForm registrationForm, Model model) {
        if (registrationForm.getEmail() == null || registrationForm.getEmail().isBlank() ||
            registrationForm.getPassword() == null || registrationForm.getPassword().isBlank()) {
            model.addAttribute("registrationError", "Inserisci email e password validi.");
            return "register";
        }

        if (credentialsRepository.findByEmail(registrationForm.getEmail()).isPresent()) {
            model.addAttribute("registrationError", "Email già in uso. Scegli un'altra email.");
            return "register";
        }

        User user = new User();
        user.setNome(registrationForm.getNome());
        user.setCognome(registrationForm.getCognome());

        Credentials credentials = new Credentials();
        credentials.setEmail(registrationForm.getEmail());
        credentials.setPassword(passwordEncoder.encode(registrationForm.getPassword()));
        credentials.setRole("USER");
        credentials.setUser(user);

        credentialsRepository.save(credentials);

        model.addAttribute("messaggioSuccesso", "Registrazione completata! Ora puoi effettuare il login.");
        return "login";
    }
}