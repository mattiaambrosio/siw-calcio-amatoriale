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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();

            if (principal instanceof OAuth2User) {
                OAuth2User oauthUser = (OAuth2User) principal;
                model.addAttribute("displayName", oauthUser.getAttribute("name"));
            } else {
                String email = auth.getName();
                credentialsRepository.findByEmail(email).ifPresent(credentials -> {
                    User user = credentials.getUser();
                    if (user != null) {
                        model.addAttribute("displayName", user.getNome() + " " + user.getCognome());
                        model.addAttribute("currentUser", user);
                    } else {
                        model.addAttribute("displayName", email);
                    }
                });
            }
        }

        return "dashboard";
    }

    @GetMapping("/benvenuto")
    public String showBenvenuto(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        Object principal = auth.getPrincipal();
        String displayName = "Utente";
        boolean isAdmin = false;

        if (principal instanceof OAuth2User) {
            OAuth2User oauthUser = (OAuth2User) principal;
            displayName = oauthUser.getAttribute("name");
            if (displayName == null)
                displayName = oauthUser.getAttribute("email");
        } else {
            String email = auth.getName();
            Credentials cred = credentialsRepository.findByEmail(email).orElse(null);
            if (cred != null) {
                User user = cred.getUser();
                if (user != null && user.getNome() != null) {
                    displayName = user.getNome() + " " + user.getCognome();
                } else {
                    displayName = email;
                }
                isAdmin = "ADMIN".equals(cred.getRole());
            }
        }

        // Verifica il ruolo anche dalle authorities di Spring Security
        if (!isAdmin) {
            isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        }

        model.addAttribute("displayName", displayName);
        model.addAttribute("isAdmin", isAdmin);
        return "benvenuto";
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

    private String getEmailLoggata() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        }
        return auth.getName();
    }

    @GetMapping("/profilo")
    public String mostraProfilo(Model model) {
        String email = getEmailLoggata();
        if (email == null)
            return "redirect:/login";

        Credentials cred = credentialsRepository.findByEmail(email).orElse(null);
        if (cred == null || cred.getUser() == null)
            return "redirect:/";

        model.addAttribute("user", cred.getUser());
        return "profilo";
    }

    @PostMapping("/profilo")
    public String salvaProfilo(@ModelAttribute User datiForm) {
        String email = getEmailLoggata();
        if (email == null)
            return "redirect:/login";

        Credentials cred = credentialsRepository.findByEmail(email).orElseThrow();
        User user = cred.getUser();
        user.setNome(datiForm.getNome());
        user.setCognome(datiForm.getCognome());
        credentialsRepository.save(cred); // cascade ALL salva anche lo User

        return "redirect:/dashboard";
    }
}