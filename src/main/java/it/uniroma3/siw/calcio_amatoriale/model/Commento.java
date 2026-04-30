package it.uniroma3.siw.calcio_amatoriale.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Commento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String testo;

    private LocalDateTime dataCreazione;

    // L'autore è identificato tramite le sue Credentials (che contengono email e ruolo)
    @ManyToOne
    @JoinColumn(name = "autore_id", nullable = false)
    private Credentials autore;

    // Un commento appartiene a una partita
    @ManyToOne
    @JoinColumn(name = "partita_id", nullable = false)
    private Partita partita;

    public Commento() {
        this.dataCreazione = LocalDateTime.now();
    }

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public Credentials getAutore() { return autore; }
    public void setAutore(Credentials autore) { this.autore = autore; }

    public Partita getPartita() { return partita; }
    public void setPartita(Partita partita) { this.partita = partita; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commento commento = (Commento) o;
        return Objects.equals(id, commento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
