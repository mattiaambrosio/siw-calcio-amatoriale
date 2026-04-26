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

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // 1. Chiunque può accedere alla Home, CSS e alle liste pubbliche
                .requestMatchers(HttpMethod.GET, "/", "/index", "/css/**", "/tornei", "/squadre", "/giocatori", "/arbitri", "/partite", "/register", "/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()
                // 2. Solo gli ADMIN possono accedere a tutto ciò che è sotto /admin/
                .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
                // 3. Tutto il resto richiede l'autenticazione
                .anyRequest().authenticated()
            )
            // Configurazione Login
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
            )
            // Configurazione Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true).permitAll()
            );

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
            .authoritiesByUsernameQuery("SELECT username, role FROM credentials WHERE username=?")
            .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
    }
}