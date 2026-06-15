package it.uniroma3.siw.calcio_amatoriale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                // Utente Google/OAuth2: legge il nome sempre dal DB (aggiornabile dall'utente)
                // e usa il token Google solo come fallback
                OAuth2User oauthUser = (OAuth2User) principal;
                String email = oauthUser.getAttribute("email");
                String displayName = null;

                if (email != null) {
                    Credentials cred = credentialsRepository.findByEmail(email).orElse(null);
                    if (cred != null && cred.getUser() != null) {
                        User dbUser = cred.getUser();
                        model.addAttribute("currentUser", dbUser);
                        // Preferisce nome+cognome dal DB se presenti
                        String nome = (dbUser.getNome() != null ? dbUser.getNome() : "");
                        String cognome = (dbUser.getCognome() != null ? dbUser.getCognome() : "");
                        String dbName = (nome + " " + cognome).trim();
                        displayName = dbName.isEmpty() ? null : dbName;
                    }
                }
                // Fallback al token Google se il DB non ha ancora un nome
                if (displayName == null) {
                    displayName = oauthUser.getAttribute("name");
                }
                if (displayName == null) {
                    displayName = email;
                }
                model.addAttribute("displayName", displayName);
            } else {
                String email = auth.getName();
                Credentials cred = credentialsRepository.findByEmail(email).orElse(null);
                if (cred != null) {
                    User user = cred.getUser();
                    if (user != null) {
                        String nome = (user.getNome() != null ? user.getNome() : "");
                        String cognome = (user.getCognome() != null ? user.getCognome() : "");
                        model.addAttribute("displayName", (nome + " " + cognome).trim());
                        model.addAttribute("currentUser", user);
                    } else {
                        model.addAttribute("displayName", email);
                    }
                } else {
                    model.addAttribute("displayName", email);
                }
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken)
            return "redirect:/login";

        String email = getEmailLoggata();
        if (email == null)
            return "redirect:/login";

        boolean isOAuth2 = auth.getPrincipal() instanceof OAuth2User;

        Credentials cred = credentialsRepository.findByEmail(email).orElse(null);
        if (cred == null)
            return "redirect:/login";

        // Edge case: User assente (non dovrebbe succedere dopo OAuth2SuccessHandler)
        if (cred.getUser() == null) {
            User nuovoUser = new User();
            if (isOAuth2 && auth.getPrincipal() instanceof OAuth2User oauthUser) {
                nuovoUser.setNome(oauthUser.getAttribute("given_name"));
                nuovoUser.setCognome(oauthUser.getAttribute("family_name"));
            }
            cred.setUser(nuovoUser);
            credentialsRepository.save(cred);
        }

        model.addAttribute("user", cred.getUser());
        model.addAttribute("email", email);
        model.addAttribute("isOAuth2", isOAuth2);
        return "profilo";
    }

    @PostMapping("/profilo")
    public String salvaProfilo(@RequestParam("nome") String nome,
                               @RequestParam("cognome") String cognome,
                               RedirectAttributes redirectAttributes) {
        String email = getEmailLoggata();
        if (email == null)
            return "redirect:/login";

        Credentials cred = credentialsRepository.findByEmail(email).orElse(null);
        if (cred == null)
            return "redirect:/login";

        User user = cred.getUser();
        if (user == null) {
            user = new User();
            cred.setUser(user);
        }
        user.setNome(nome.trim());
        user.setCognome(cognome.trim());
        credentialsRepository.save(cred); // cascade ALL salva anche lo User

        redirectAttributes.addFlashAttribute("successMessage", "Profilo aggiornato con successo!");
        return "redirect:/profilo";
    }

    @PostMapping("/profilo/password")
    public String cambiaPassword(@RequestParam("passwordAttuale") String passwordAttuale,
                                 @RequestParam("nuovaPassword") String nuovaPassword,
                                 @RequestParam("confermaPassword") String confermaPassword,
                                 RedirectAttributes redirectAttributes) {
        String email = getEmailLoggata();
        if (email == null)
            return "redirect:/login";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Gli utenti OAuth2 non possono cambiare la password
        if (auth.getPrincipal() instanceof OAuth2User) {
            redirectAttributes.addFlashAttribute("passwordError", "Gli account Google non possono cambiare la password da qui.");
            return "redirect:/profilo";
        }

        Credentials cred = credentialsRepository.findByEmail(email).orElse(null);
        if (cred == null)
            return "redirect:/login";

        // Verifica la password attuale
        if (!passwordEncoder.matches(passwordAttuale, cred.getPassword())) {
            redirectAttributes.addFlashAttribute("passwordError", "La password attuale non è corretta.");
            return "redirect:/profilo";
        }

        // Verifica che le nuove password coincidano
        if (!nuovaPassword.equals(confermaPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "Le nuove password non coincidono.");
            return "redirect:/profilo";
        }

        // Verifica lunghezza minima
        if (nuovaPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("passwordError", "La nuova password deve essere di almeno 6 caratteri.");
            return "redirect:/profilo";
        }

        cred.setPassword(passwordEncoder.encode(nuovaPassword));
        credentialsRepository.save(cred);

        redirectAttributes.addFlashAttribute("passwordSuccess", "Password aggiornata con successo!");
        return "redirect:/profilo";
    }
}