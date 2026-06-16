package it.uniroma3.siw.calcio_amatoriale.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Arbitro {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "Il codice arbitrale è obbligatorio")
    private String codiceArbitrale; // Identificativo univoco dell'arbitro

    @OneToMany(mappedBy = "arbitro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partita> partiteArbitrate; // Un arbitro dirige più partite

    public Arbitro() {}

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getCodiceArbitrale() { return codiceArbitrale; }
    public void setCodiceArbitrale(String codiceArbitrale) { this.codiceArbitrale = codiceArbitrale; }

    public List<Partita> getPartiteArbitrate() { return partiteArbitrate; }
    public void setPartiteArbitrate(List<Partita> partiteArbitrate) { this.partiteArbitrate = partiteArbitrate; }

    // Equals e HashCode basati sul codice arbitrale, che è univoco
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbitro arbitro = (Arbitro) o;
        return Objects.equals(codiceArbitrale, arbitro.codiceArbitrale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codiceArbitrale);
    }
}