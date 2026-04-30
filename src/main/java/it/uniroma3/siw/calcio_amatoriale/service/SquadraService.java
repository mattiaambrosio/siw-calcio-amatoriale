package it.uniroma3.siw.calcio_amatoriale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.calcio_amatoriale.model.Squadra;
import it.uniroma3.siw.calcio_amatoriale.repository.SquadraRepository;

@Service
public class SquadraService {

    @Autowired
    private SquadraRepository squadraRepository;

    @Transactional
    public void save(Squadra squadra) {
        squadraRepository.save(squadra);
    }

    @Transactional
    public void delete(Long id) {
        squadraRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Squadra findById(Long id) {
        return squadraRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Iterable<Squadra> findAll() {
        return squadraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean alreadyExists(Squadra squadra) {
        return squadraRepository.existsByNome(squadra.getNome());
    }
}