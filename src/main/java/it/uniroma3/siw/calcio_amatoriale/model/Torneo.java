package it.uniroma3.siw.calcio_amatoriale.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany; // Aggiunto questo
import jakarta.persistence.CascadeType; // Aggiunto questo
import jakarta.validation.constraints.*;

import java.util.Objects;
import java.util.List;

@Entity
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotNull(message = "L'anno è obbligatorio")
    @Min(value = 1900, message = "Anno minimo 1900")
    @Max(value = 2100, message = "Anno massimo 2100")
    private Integer anno;

    private String descrizione;

    @ManyToMany(mappedBy = "tornei")
    private List<Squadra> squadre;

    // --- NUOVA AGGIUNTA QUI ---
    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partita> partite;
    // --------------------------

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public List<Squadra> getSquadre() { return squadre; }
    public void setSquadre(List<Squadra> squadre) { this.squadre = squadre; }

    // --- NUOVI GETTER E SETTER PER LE PARTITE ---
    public List<Partita> getPartite() { return partite; }
    public void setPartite(List<Partita> partite) { this.partite = partite; }
    // --------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Torneo torneo = (Torneo) o;
        return Objects.equals(nome, torneo.nome) && Objects.equals(anno, torneo.anno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, anno);
    }
}