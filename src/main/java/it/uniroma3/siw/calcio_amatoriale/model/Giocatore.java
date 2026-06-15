package it.uniroma3.siw.calcio_amatoriale.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Giocatore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @NotNull(message = "La data di nascita è obbligatoria")
    @Past(message = "La data deve essere nel passato")
    private LocalDate dataDiNascita;

    @NotBlank(message = "Il ruolo è obbligatorio")
    private String ruolo;

    @NotNull(message = "L'altezza è obbligatoria")
    @Positive(message = "L'altezza deve essere positiva")
    @Max(value = 250, message = "Altezza non valida")
    private Integer altezza;      // In centimetri

    @ManyToOne
    private Squadra squadra;      // Ogni giocatore appartiene a una sola squadra 

    public Giocatore() {}

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public LocalDate getDataDiNascita() { return dataDiNascita; }
    public void setDataDiNascita(LocalDate dataDiNascita) { this.dataDiNascita = dataDiNascita; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

    public Integer getAltezza() { return altezza; }
    public void setAltezza(Integer altezza) { this.altezza = altezza; }

    public Squadra getSquadra() { return squadra; }
    public void setSquadra(Squadra squadra) { this.squadra = squadra; }

    // Equals e HashCode (Usiamo nome, cognome e data di nascita per identificare un giocatore univoco)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Giocatore giocatore = (Giocatore) o;
        return Objects.equals(nome, giocatore.nome) && 
               Objects.equals(cognome, giocatore.cognome) && 
               Objects.equals(dataDiNascita, giocatore.dataDiNascita);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cognome, dataDiNascita);
    }
}