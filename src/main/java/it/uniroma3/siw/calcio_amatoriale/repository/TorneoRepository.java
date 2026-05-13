package it.uniroma3.siw.calcio_amatoriale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.calcio_amatoriale.model.Torneo;

public interface TorneoRepository extends CrudRepository<Torneo, Long> {

    public boolean existsByNome(String nome);

    // ── ANALISI SPERIMENTALE: Strategia LAZY (default) ──────────────────────
    // Usa findAll() standard ereditato da CrudRepository → LAZY fetch.
    // Ogni accesso a torneo.getSquadre() genererà una query aggiuntiva → N+1.

    // ── Strategia JOIN FETCH (1 sola query JPQL) ─────────────────────────────
    // DISTINCT evita duplicati per la relazione ManyToMany
    @Query("SELECT DISTINCT t FROM Torneo t LEFT JOIN FETCH t.squadre")
    List<Torneo> findAllWithSquadreJoinFetch();

    // ── Strategia LAZY pura (per l'analisi sperimentale) ─────────────────────
    // Query JPQL senza EntityGraph: Hibernate userà il fetch LAZY di default
    @Query("SELECT t FROM Torneo t")
    List<Torneo> findAllLazy();

    // ── Strategia EntityGraph (1 sola query, più dichiarativa) ───────────────
    // Spring genera automaticamente una JOIN FETCH basandosi sull'attributo
    @EntityGraph(attributePaths = {"squadre"})
    List<Torneo> findAll();
}