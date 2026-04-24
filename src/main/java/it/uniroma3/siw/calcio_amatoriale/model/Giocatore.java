package it.uniroma3.siw.calcio_amatoriale.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Giocatore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;          // [cite: 35]
    private String cognome;       // [cite: 36]
    private LocalDate dataDiNascita; // [cite: 37]
    private String ruolo;         // [cite: 38]
    private Integer altezza;      // In centimetri [cite: 39]

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