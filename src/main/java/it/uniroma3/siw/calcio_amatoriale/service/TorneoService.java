package it.uniroma3.siw.calcio_amatoriale.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.calcio_amatoriale.model.Squadra;
import it.uniroma3.siw.calcio_amatoriale.model.Torneo;
import it.uniroma3.siw.calcio_amatoriale.repository.SquadraRepository;
import it.uniroma3.siw.calcio_amatoriale.repository.TorneoRepository;

@Service
public class TorneoService {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private SquadraRepository squadraRepository;

    @Transactional
    public void save(Torneo torneo) {
        torneoRepository.save(torneo);
    }

    @Transactional
    public void delete(Long id) {
        torneoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Torneo findById(Long id) {
        return torneoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Iterable<Torneo> findAll() {
        return torneoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean alreadyExists(Torneo torneo) {
        return torneoRepository.existsByNome(torneo.getNome());
    }

    /**
     * Iscrive una squadra a un torneo.
     * Tutta la logica transazionale è gestita qui nel Service Layer (§6 della consegna).
     * @return "OK" se l'iscrizione è avvenuta, "GIA_ISCRITTA" se la squadra era già nel torneo
     */
    @Transactional
    public String iscriviSquadraATorneo(Long torneoId, Long squadraId) {
        Torneo torneo = torneoRepository.findById(torneoId).orElse(null);
        Squadra squadra = squadraRepository.findById(squadraId).orElse(null);

        if (torneo == null || squadra == null) {
            return "NON_TROVATO";
        }

        // Inizializziamo la lista se è null per evitare NullPointerException
        if (squadra.getTornei() == null) {
            squadra.setTornei(new ArrayList<>());
        }

        // Controlliamo che la squadra non sia già iscritta a questo torneo
        if (squadra.getTornei().contains(torneo)) {
            return "GIA_ISCRITTA";
        }

        // Sincronizziamo entrambi i lati della relazione ManyToMany
        squadra.getTornei().add(torneo);
        torneo.getSquadre().add(squadra);
        squadraRepository.save(squadra);

        return "OK";
    }
}