package it.uniroma3.siw.calcio_amatoriale.service;

import java.util.List;

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

    @Transactional
    public void delete(Long id) {
        arbitroRepository.deleteById(id);
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

    @Transactional(readOnly = true)
    public List<Arbitro> cerca(String search) {
        if (search == null || search.isBlank()) {
            return (List<Arbitro>) arbitroRepository.findAll();
        }
        return arbitroRepository.findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(search, search);
    }
}