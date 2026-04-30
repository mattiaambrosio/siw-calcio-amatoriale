package it.uniroma3.siw.calcio_amatoriale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.calcio_amatoriale.model.Arbitro;
import it.uniroma3.siw.calcio_amatoriale.repository.ArbitroRepository;

@Service
public class ArbitroService {

    @Autowired
    private ArbitroRepository arbitroRepository;

    @Transactional
    public void save(Arbitro arbitro) {
        arbitroRepository.save(arbitro);
    }

    @Transactional(readOnly = true)
    public Arbitro findById(Long id) {
        return arbitroRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Iterable<Arbitro> findAll() {
        return arbitroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean alreadyExists(Arbitro arbitro) {
        return arbitroRepository.existsByCodiceArbitrale(arbitro.getCodiceArbitrale());
    }
}