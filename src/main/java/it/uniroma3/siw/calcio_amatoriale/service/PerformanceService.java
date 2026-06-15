package it.uniroma3.siw.calcio_amatoriale.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.calcio_amatoriale.model.StrategiaResult;
import it.uniroma3.siw.calcio_amatoriale.model.Torneo;
import it.uniroma3.siw.calcio_amatoriale.repository.TorneoRepository;

// Service per il confronto delle strategie JPA: LAZY, JOIN FETCH, EntityGraph
@Service
public class PerformanceService {

    private static final Logger log = LoggerFactory.getLogger(PerformanceService.class);

    @Autowired
    private TorneoRepository torneoRepository;

    // STRATEGIA 1 — LAZY (default JPA): genera N+1 query
    // 1 query per i tornei + 1 query per le squadre di ogni torneo
    @Transactional(readOnly = true)
    public StrategiaResult testLazy() {
        log.info("=== [ANALISI LAZY] Inizio ===");
        long start = System.currentTimeMillis();

        // findAllLazy() usa JPQL senza @EntityGraph: fetch LAZY puro
        List<Torneo> tornei = torneoRepository.findAllLazy();

        // L'accesso alle squadre provoca le query N+1
        int totaleSquadre = 0;
        for (Torneo t : tornei) {
            totaleSquadre += t.getSquadre() != null ? t.getSquadre().size() : 0;
        }

        long fine = System.currentTimeMillis();
        long tempo = fine - start;
        log.info("=== [ANALISI LAZY] Fine — {}ms, {} tornei, {} squadre totali ===",
                tempo, tornei.size(), totaleSquadre);

        StrategiaResult res = new StrategiaResult(
                "1. LAZY (default)",
                "Hibernate carica le collection solo al primo accesso. Con N tornei genera 1 query " +
                "per i tornei + 1 query per le squadre di ciascun torneo → problema N+1 query. " +
                "Problema: se ci sono 10 tornei, vengono eseguite 11 query totali.",
                "SELECT t.* FROM torneo t;\n" +
                "-- poi per ogni torneo:\n" +
                "SELECT s.* FROM squadra s JOIN iscrizione_squadra_torneo ist ON ... WHERE ist.torneo_id = ?;"
        );
        res.setTempoMs(tempo);
        res.setNumTornei(tornei.size());
        res.setNumTotaleSquadre(totaleSquadre);
        return res;
    }

    // STRATEGIA 2 — JOIN FETCH (query JPQL custom): 1 sola query con JOIN
    // Elimina il problema N+1; DISTINCT previene i duplicati del ManyToMany
    @Transactional(readOnly = true)
    public StrategiaResult testJoinFetch() {
        log.info("=== [ANALISI JOIN FETCH] Inizio ===");
        long start = System.currentTimeMillis();

        List<Torneo> tornei = torneoRepository.findAllWithSquadreJoinFetch();

        int totaleSquadre = 0;
        for (Torneo t : tornei) {
            totaleSquadre += t.getSquadre() != null ? t.getSquadre().size() : 0;
        }

        long fine = System.currentTimeMillis();
        long tempo = fine - start;
        log.info("=== [ANALISI JOIN FETCH] Fine — {}ms, {} tornei, {} squadre totali ===",
                tempo, tornei.size(), totaleSquadre);

        StrategiaResult res = new StrategiaResult(
                "2. JOIN FETCH (JPQL custom)",
                "Una singola query JPQL con LEFT JOIN FETCH carica Tornei e Squadre in un unico " +
                "round-trip al database. DISTINCT previene i duplicati causati dalla relazione " +
                "ManyToMany. Soluzione ottimale per leggibilità e prestazioni.",
                "SELECT DISTINCT t FROM Torneo t LEFT JOIN FETCH t.squadre\n" +
                "-- tradotto da Hibernate in:\n" +
                "SELECT DISTINCT t.*, s.* FROM torneo t\n" +
                "LEFT JOIN iscrizione_squadra_torneo ist ON t.id = ist.torneo_id\n" +
                "LEFT JOIN squadra s ON ist.squadra_id = s.id;"
        );
        res.setTempoMs(tempo);
        res.setNumTornei(tornei.size());
        res.setNumTotaleSquadre(totaleSquadre);
        return res;
    }

    // STRATEGIA 3 — EntityGraph (@EntityGraph annotation): 1 sola query dichiarativa
    // Approccio più pulito rispetto al JOIN FETCH JPQL manuale
    @Transactional(readOnly = true)
    public StrategiaResult testEntityGraph() {
        log.info("=== [ANALISI ENTITY GRAPH] Inizio ===");
        long start = System.currentTimeMillis();

        // findAll() nel repository usa @EntityGraph(attributePaths = {"squadre"})
        List<Torneo> tornei = torneoRepository.findAll();

        int totaleSquadre = 0;
        for (Torneo t : tornei) {
            totaleSquadre += t.getSquadre() != null ? t.getSquadre().size() : 0;
        }

        long fine = System.currentTimeMillis();
        long tempo = fine - start;
        log.info("=== [ANALISI ENTITY GRAPH] Fine — {}ms, {} tornei, {} squadre totali ===",
                tempo, tornei.size(), totaleSquadre);

        StrategiaResult res = new StrategiaResult(
                "3. EntityGraph (@EntityGraph annotation)",
                "L'annotazione @EntityGraph(attributePaths = {\"squadre\"}) sul metodo del repository " +
                "istruisce Hibernate a caricare le squadre con un LEFT OUTER JOIN. Approccio " +
                "dichiarativo: nessuna query JPQL manuale. Genera lo stesso numero di query della " +
                "strategia JOIN FETCH (1 sola), con maggiore separazione delle responsabilità.",
                "-- Generato automaticamente da @EntityGraph:\n" +
                "SELECT DISTINCT t.*, s.* FROM torneo t\n" +
                "LEFT OUTER JOIN iscrizione_squadra_torneo ist ON t.id = ist.torneo_id\n" +
                "LEFT OUTER JOIN squadra s ON ist.squadra_id = s.id;"
        );
        res.setTempoMs(tempo);
        res.setNumTornei(tornei.size());
        res.setNumTotaleSquadre(totaleSquadre);
        return res;
    }

    // Esegue le tre strategie in sequenza e restituisce i risultati comparati
    public List<StrategiaResult> runFullAnalysis() {
        List<StrategiaResult> risultati = new ArrayList<>();
        risultati.add(testLazy());
        risultati.add(testJoinFetch());
        risultati.add(testEntityGraph());
        return risultati;
    }
}
