package it.uniroma3.siw.calcio_amatoriale.authentication;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import it.uniroma3.siw.calcio_amatoriale.model.Credentials;
import it.uniroma3.siw.calcio_amatoriale.model.User;
import it.uniroma3.siw.calcio_amatoriale.repository.CredentialsRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Solo se il login è avvenuto tramite Google (OAuth2)
        if (authentication.getPrincipal() instanceof OAuth2User oauthUser) {
            String email = oauthUser.getAttribute("email");

            if (email != null && credentialsRepository.findByEmail(email).isEmpty()) {
                // Primo accesso con Google: creo l'utente nel nostro database
                User user = new User();
                user.setNome(oauthUser.getAttribute("given_name"));
                user.setCognome(oauthUser.getAttribute("family_name"));

                Credentials credentials = new Credentials();
                credentials.setEmail(email);
                credentials.setPassword("");      // niente password: login solo via Google
                credentials.setRole("USER");
                credentials.setUser(user);

                credentialsRepository.save(credentials);
            }
        }

        // Prosegue verso la pagina di destinazione (/benvenuto)
        super.setDefaultTargetUrl("/benvenuto");
        super.setAlwaysUseDefaultTargetUrl(true);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}