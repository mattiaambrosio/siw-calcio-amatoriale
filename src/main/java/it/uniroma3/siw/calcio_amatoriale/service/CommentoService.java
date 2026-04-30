package it.uniroma3.siw.calcio_amatoriale.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.calcio_amatoriale.model.Commento;
import it.uniroma3.siw.calcio_amatoriale.repository.CommentoRepository;

@Service
public class CommentoService {

    @Autowired
    private CommentoRepository commentoRepository;

    /** Restituisce tutti i commenti di una partita, dal più recente. */
    @Transactional(readOnly = true)
    public List<Commento> findByPartita(Long partitaId) {
        return commentoRepository.findByPartitaIdOrderByDataCreazioneDesc(partitaId);
    }

    /** Trova un singolo commento per ID. */
    @Transactional(readOnly = true)
    public Commento findById(Long id) {
        return commentoRepository.findById(id).orElse(null);
    }

    /** Verifica se l'utente con questa email è l'autore del commento. */
    @Transactional(readOnly = true)
    public boolean isOwner(Long commentoId, String email) {
        return commentoRepository.isOwner(commentoId, email);
    }

    /** Salva un nuovo commento o aggiorna uno esistente. */
    @Transactional
    public void save(Commento commento) {
        commentoRepository.save(commento);
    }

    /** Elimina un commento solo se l'utente ne è l'autore (o è admin). */
    @Transactional
    public boolean deleteIfOwner(Long commentoId, String email) {
        if (commentoRepository.isOwner(commentoId, email)) {
            commentoRepository.deleteById(commentoId);
            return true;
        }
        return false;
    }
}
