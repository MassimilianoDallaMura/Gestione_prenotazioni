package backend_esame_3.Gestione_prenotazioni.service;

import backend_esame_3.Gestione_prenotazioni.bean.Postazione;
import backend_esame_3.Gestione_prenotazioni.bean.Prenotazione;
import backend_esame_3.Gestione_prenotazioni.bean.Utente;
import backend_esame_3.Gestione_prenotazioni.repositiry.PostazioneRepository;
import backend_esame_3.Gestione_prenotazioni.repositiry.PrenotazioneRepository;
import backend_esame_3.Gestione_prenotazioni.repositiry.UtenteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PrenotazioneService {
    @Autowired
    private PrenotazioneRepository prenotazioneRepository;
    @Autowired
    private PostazioneRepository postazioneRepository;
    @Autowired
    private UtenteRepository utenteRepository;
//    @Autowired
//    private Prenotazione prenotazione;


    public void inserisciPrenotazione(Prenotazione prenotazione) {
        prenotazioneRepository.save(prenotazione);
    }

    public Prenotazione getPrenotazione(Long id){
        return prenotazioneRepository.findById(id).orElse(null);
    }

    public List<Prenotazione> getPrenotazioni(){
        return prenotazioneRepository.findAll();
    }

    public void cancellaPrenotazione(Long id){
        prenotazioneRepository.deleteById(id);
    }

    public List<Prenotazione> findPrenotazioniByDataAndPostazione(LocalDate data, Postazione postazione) {
        return prenotazioneRepository.findByDataAndPostazione(data, postazione);
    }

    @Transactional
    public void prenotaPostazione(Postazione postazione, Utente utente, LocalDate dataPrenotazione, String codicePrenotazione) {
        if (postazione != null && utente != null) {
            Prenotazione prenotazione = new Prenotazione();
            prenotazione.setDataPrenotazione(dataPrenotazione);
            prenotazione.setPostazione(postazione);
            prenotazione.setUtente(utente);

            // Imposta la data di scadenza per 24 ore
            LocalDateTime scadenza = LocalDateTime.now().plusHours(24);
            prenotazione.setDataScadenza(scadenza);

            // Aggiorna lo stato della postazione
            postazione.setLibera(false);

            // Imposta il codice della prenotazione per l'utente
            utente.setCodicePrenotazione(codicePrenotazione);
            prenotazione.setCodicePrenotazione(Collections.singletonList(codicePrenotazione));

            // Salva la prenotazione e aggiorna la postazione
            prenotazioneRepository.save(prenotazione);
            postazioneRepository.save(postazione);

            // Aggiorna l'utente
            utenteRepository.save(utente);

            System.out.println("Prenotazione creata con successo!");
        } else {
            System.out.println("Postazione o Utente non trovato.");
        }
    }
}
