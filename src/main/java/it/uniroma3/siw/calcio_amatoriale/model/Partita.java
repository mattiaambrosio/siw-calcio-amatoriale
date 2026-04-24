package it.uniroma3.siw.calcio_amatoriale.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Partita {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime dataEOra; // Usiamo LocalDateTime per avere sia giorno che orario
    private String luogo;           // 
    private Integer goalsHome;      // 
    private Integer goalsAway;      // 
    private String stato;           // Es. "SCHEDULED", "PLAYED"

    @ManyToOne
    private Torneo torneo;          // Una partita appartiene a un torneo

    @ManyToOne
    private Squadra squadraCasa;    // Prima squadra coinvolta

    @ManyToOne
    private Squadra squadraOspite;  // Seconda squadra coinvolta

  
    @ManyToOne
    private Arbitro arbitro;

    public Partita() {}

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataEOra() { return dataEOra; }
    public void setDataEOra(LocalDateTime dataEOra) { this.dataEOra = dataEOra; }

    public String getLuogo() { return luogo; }
    public void setLuogo(String luogo) { this.luogo = luogo; }

    public Integer getGoalsHome() { return goalsHome; }
    public void setGoalsHome(Integer goalsHome) { this.goalsHome = goalsHome; }

    public Integer getGoalsAway() { return goalsAway; }
    public void setGoalsAway(Integer goalsAway) { this.goalsAway = goalsAway; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public Torneo getTorneo() { return torneo; }
    public void setTorneo(Torneo torneo) { this.torneo = torneo; }

    public Squadra getSquadraCasa() { return squadraCasa; }
    public void setSquadraCasa(Squadra squadraCasa) { this.squadraCasa = squadraCasa; }

    public Squadra getSquadraOspite() { return squadraOspite; }
    public void setSquadraOspite(Squadra squadraOspite) { this.squadraOspite = squadraOspite; }

    public Arbitro getArbitro() { return arbitro; }
    public void setArbitro(Arbitro arbitro) { this.arbitro = arbitro; }
    
    // Equals e HashCode (basati su Torneo, Squadre e Data)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partita partita = (Partita) o;
        return Objects.equals(dataEOra, partita.dataEOra) && 
               Objects.equals(squadraCasa, partita.squadraCasa) && 
               Objects.equals(squadraOspite, partita.squadraOspite) &&
               Objects.equals(torneo, partita.torneo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataEOra, squadraCasa, squadraOspite, torneo);
    }

}