package it.uniroma3.siw.calcio_amatoriale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.calcio_amatoriale.model.Giocatore;
import it.uniroma3.siw.calcio_amatoriale.repository.GiocatoreRepository;

@Service
public class GiocatoreService {

    @Autowired
    private GiocatoreRepository giocatoreRepository;

    @Transactional
    public void save(Giocatore giocatore) {
        giocatoreRepository.save(giocatore);
    }

    @Transactional
    public Giocatore findById(Long id) {
        return giocatoreRepository.findById(id).orElse(null);
    }

    @Transactional
    public Iterable<Giocatore> findAll() {
        return giocatoreRepository.findAll();
    }

    @Transactional
    public boolean alreadyExists(Giocatore giocatore) {
        return giocatoreRepository.existsByNomeAndCognome(giocatore.getNome(), giocatore.getCognome());
    }
}