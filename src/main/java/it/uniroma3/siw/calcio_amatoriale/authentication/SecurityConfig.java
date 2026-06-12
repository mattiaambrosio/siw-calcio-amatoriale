package it.uniroma3.siw.calcio_amatoriale.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    // ---------------------------------------------------------------
    // UserDetailsService tramite JDBC (sostituisce configureGlobal)
    // ---------------------------------------------------------------
    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(
                "SELECT email AS username, password, true AS enabled FROM credentials WHERE email = ?");
        manager.setAuthoritiesByUsernameQuery(
                "SELECT email AS username, role AS authority FROM credentials WHERE email = ?");
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        // Spring Security 7: DaoAuthenticationProvider usa il costruttore con parametri
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    // ---------------------------------------------------------------
    // Security Filter Chain
    // ---------------------------------------------------------------
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();
        csrfHandler.setCsrfRequestAttributeName(null);

        http
            .csrf(csrf -> csrf
                    .csrfTokenRequestHandler(csrfHandler))

            .authorizeHttpRequests(authorize -> authorize

                    // Risorse statiche e pagine pubbliche
                    .requestMatchers(HttpMethod.GET,
                            "/", "/index",
                            "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico",
                            "/tornei", "/torneo/**",
                            "/squadre", "/squadra/**",
                            "/giocatori", "/giocatore/**",
                            "/partite", "/partita/**",
                            "/arbitri", "/arbitro/**",
                            "/classifica/**",
                            "/api/partita/*/commenti", "/api/me",
                            "/register", "/login",
                            "/error",
                            "/oauth2/**", "/login/oauth2/**")
                    .permitAll()

                    // POST pubblici
                    .requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()

                    // API protette
                    .requestMatchers(HttpMethod.POST,   "/api/**").authenticated()
                    .requestMatchers(HttpMethod.PUT,    "/api/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()

                    // Admin
                    .requestMatchers("/admin/**").hasAuthority("ADMIN")

                    // Tutto il resto richiede autenticazione
                    .anyRequest().authenticated())

            // Form login
            .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/benvenuto", true)
                    .failureUrl("/login?error=true")
                    .permitAll())

            // OAuth2 Google
            .oauth2Login(oauth2 -> oauth2
                    .loginPage("/login")
                    .defaultSuccessUrl("/benvenuto", true))

            // Logout
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .clearAuthentication(true)
                    .permitAll());

        return http.build();
    }
}