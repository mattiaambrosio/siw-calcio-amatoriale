package it.uniroma3.siw.calcio_amatoriale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.calcio_amatoriale.model.*;
import it.uniroma3.siw.calcio_amatoriale.service.*;
import it.uniroma3.siw.calcio_amatoriale.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CalcioAmatorialeApplicationTests {

    @Autowired
    private TorneoService torneoService;

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private GiocatoreService giocatoreService;

    @Autowired
    private PartitaService partitaService;

    @Autowired
    private ArbitroService arbitroService;

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private SquadraRepository squadraRepository;

    @Autowired
    private PartitaRepository partitaRepository;

    @Autowired
    private GiocatoreRepository giocatoreRepository;

    @Autowired
    private ArbitroRepository arbitroRepository;

    @Test
    void contextLoads() {
    }

    @Test
    @Transactional
    void testCascadeDeletions() {
        // 1. Create a Torneo
        Torneo torneo = new Torneo();
        torneo.setNome("Torneo Test Cascade");
        torneo.setAnno(2026);
        torneo.setDescrizione("Test description");
        torneo.setSquadre(new ArrayList<>());
        torneo.setPartite(new ArrayList<>());
        torneoService.save(torneo);

        // 2. Create Teams
        Squadra homeTeam = new Squadra();
        homeTeam.setNome("Squadra Home Test");
        homeTeam.setAnnoDiFondazione(2000);
        homeTeam.setCitta("Roma");
        homeTeam.setTornei(new ArrayList<>());
        homeTeam.setGiocatori(new ArrayList<>());
        squadraService.save(homeTeam);

        Squadra awayTeam = new Squadra();
        awayTeam.setNome("Squadra Away Test");
        awayTeam.setAnnoDiFondazione(2001);
        awayTeam.setCitta("Milano");
        awayTeam.setTornei(new ArrayList<>());
        awayTeam.setGiocatori(new ArrayList<>());
        squadraService.save(awayTeam);

        // 3. Enroll teams in Torneo
        torneoService.iscriviSquadraATorneo(torneo.getId(), homeTeam.getId());
        torneoService.iscriviSquadraATorneo(torneo.getId(), awayTeam.getId());

        // Refresh references
        torneo = torneoService.findById(torneo.getId());
        homeTeam = squadraService.findById(homeTeam.getId());
        awayTeam = squadraService.findById(awayTeam.getId());

        // 4. Create a Player for home team
        Giocatore player = new Giocatore();
        player.setNome("Mario");
        player.setCognome("Rossi");
        player.setDataDiNascita(LocalDate.of(1995, 5, 10));
        player.setRuolo("ATTACCANTE");
        player.setAltezza(180);
        player.setSquadra(homeTeam);
        giocatoreService.save(player);
        
        homeTeam.getGiocatori().add(player);
        squadraService.save(homeTeam);

        // 5. Create an Referee
        Arbitro referee = new Arbitro();
        referee.setNome("Nicola");
        referee.setCognome("Rizzoli");
        referee.setCodiceArbitrale("ARB-9999");
        referee.setPartiteArbitrate(new ArrayList<>());
        arbitroService.save(referee);

        // 6. Create a Match
        Partita match = new Partita();
        match.setTorneo(torneo);
        match.setSquadraCasa(homeTeam);
        match.setSquadraOspite(awayTeam);
        match.setArbitro(referee);
        match.setDataEOra(LocalDateTime.now());
        match.setLuogo("Stadio Test");
        match.setGoalsHome(0);
        match.setGoalsAway(0);
        match.setStato("SCHEDULED");
        partitaService.save(match);

        torneo.getPartite().add(match);
        torneoService.save(torneo);

        Long torneoId = torneo.getId();
        Long matchId = match.getId();
        Long homeTeamId = homeTeam.getId();
        Long playerId = player.getId();
        Long refereeId = referee.getId();

        // Check everything is persisted
        assertNotNull(torneoRepository.findById(torneoId).orElse(null));
        assertNotNull(partitaRepository.findById(matchId).orElse(null));
        assertNotNull(giocatoreRepository.findById(playerId).orElse(null));

        // Let's test Torneo deletion:
        // This must delete the Torneo and cascade delete the Partita, but keep the Squadra and Arbitro.
        torneoService.delete(torneoId);

        assertNull(torneoRepository.findById(torneoId).orElse(null), "Torneo should be deleted");
        assertNull(partitaRepository.findById(matchId).orElse(null), "Partita should be cascade deleted when Torneo is deleted");
        assertNotNull(squadraRepository.findById(homeTeamId).orElse(null), "Squadra should NOT be deleted");
        assertNotNull(arbitroRepository.findById(refereeId).orElse(null), "Arbitro should NOT be deleted");

        // Let's test Squadra deletion:
        // This must delete the Squadra and cascade delete its Giocatori.
        squadraService.delete(homeTeamId);

        assertNull(squadraRepository.findById(homeTeamId).orElse(null), "Squadra should be deleted");
        assertNull(giocatoreRepository.findById(playerId).orElse(null), "Giocatore should be cascade deleted when Squadra is deleted");

        // Clean up remaining team and referee to avoid test data pollution
        squadraService.delete(awayTeam.getId());
        arbitroService.delete(refereeId);
    }
}
