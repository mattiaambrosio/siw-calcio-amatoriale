package it.uniroma3.siw.calcio_amatoriale.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Squadra {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotNull(message = "L'anno di fondazione è obbligatorio")
    @Min(value = 1850, message = "Anno minimo 1850")
    @Max(value = 2100, message = "Anno massimo 2100")
    private Integer annoDiFondazione;

    @NotBlank(message = "La città è obbligatoria")
    private String citta; 
    
    @OneToMany(mappedBy = "squadra")
    private List<Giocatore> giocatori;

   @ManyToMany
    @JoinTable(
        name = "iscrizione_squadra_torneo", // Costringe il database a creare una tabella nuova e funzionante
        joinColumns = @JoinColumn(name = "squadra_id"),
        inverseJoinColumns = @JoinColumn(name = "torneo_id")
    )
    private java.util.List<Torneo> tornei = new java.util.ArrayList<>();
    // Costruttore vuoto richiesto da JPA
    public Squadra() {}

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getAnnoDiFondazione() { return annoDiFondazione; }
    public void setAnnoDiFondazione(Integer annoDiFondazione) { this.annoDiFondazione = annoDiFondazione; }

    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }

    public List<Giocatore> getGiocatori() { return giocatori; }
    public void setGiocatori(List<Giocatore> giocatori) { this.giocatori = giocatori; }

    public List<Torneo> getTornei() { return tornei; }
    public void setTornei(List<Torneo> tornei) { this.tornei = tornei; }

    // Equals e HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Squadra squadra = (Squadra) o;
        return Objects.equals(nome, squadra.nome) && Objects.equals(annoDiFondazione, squadra.annoDiFondazione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, annoDiFondazione);
    }
}