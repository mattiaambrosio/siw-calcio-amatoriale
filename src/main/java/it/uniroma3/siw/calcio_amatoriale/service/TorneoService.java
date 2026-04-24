package it.uniroma3.siw.calcio_amatoriale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.calcio_amatoriale.model.Torneo;
import it.uniroma3.siw.calcio_amatoriale.repository.TorneoRepository;

@Service
public class TorneoService {

    @Autowired
    private TorneoRepository torneoRepository;

    @Transactional
    public void save(Torneo torneo) {
        torneoRepository.save(torneo);
    }

    @Transactional
    public Torneo findById(Long id) {
        return torneoRepository.findById(id).orElse(null);
    }

    @Transactional
    public Iterable<Torneo> findAll() {
        return torneoRepository.findAll();
    }

    @Transactional
    public boolean alreadyExists(Torneo torneo) {
        return torneoRepository.existsByNome(torneo.getNome());
    }
}