package it.uniroma3.siw.calcio_amatoriale.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // Usiamo il CsrfTokenRequestAttributeHandler plain (senza XOR)
        // per compatibilità con le richieste AJAX di React che leggono
        // il token dai meta tag Thymeleaf e lo inviano nell'header.
        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();
        csrfHandler.setCsrfRequestAttributeName(null); // Forza il caricamento eager del token

        http
                .csrf(csrf -> csrf
                        .csrfTokenRequestHandler(csrfHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/", "/index", "/css/**", "/js/**", "/tornei", "/torneo/**", "/squadra/**", "/partita/**", "/api/partita/*/commenti", "/api/me", "/register", "/login")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
                        .anyRequest().authenticated())
                // ACCESSO TRADIZIONALE (Email/Password)
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/benvenuto", true))
                // NUOVO: ACCESSO TRAMITE GOOGLE
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/benvenuto", true))
                // LOGOUT
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true).permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .authoritiesByUsernameQuery("SELECT email as username, role FROM credentials WHERE email=?")
                .usersByUsernameQuery(
                        "SELECT email as username, password, 1 as enabled FROM credentials WHERE email=?");
    }
}