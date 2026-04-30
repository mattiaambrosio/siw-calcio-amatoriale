package it.uniroma3.siw.calcio_amatoriale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.calcio_amatoriale.model.Partita;
import it.uniroma3.siw.calcio_amatoriale.repository.PartitaRepository;

@Service
public class PartitaService {

    @Autowired
    private PartitaRepository partitaRepository;

    @Transactional
    public void save(Partita partita) {
        partitaRepository.save(partita);
    }

    @Transactional
    public void delete(Long id) {
        partitaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Partita findById(Long id) {
        return partitaRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Iterable<Partita> findAll() {
        return partitaRepository.findAll();
    }
}