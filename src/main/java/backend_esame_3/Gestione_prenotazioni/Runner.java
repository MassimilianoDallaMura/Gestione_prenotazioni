package backend_esame_3.Gestione_prenotazioni;
import backend_esame_3.Gestione_prenotazioni.bean.Edificio;
import backend_esame_3.Gestione_prenotazioni.bean.Postazione;
import backend_esame_3.Gestione_prenotazioni.bean.Prenotazione;
import backend_esame_3.Gestione_prenotazioni.bean.Utente;
import backend_esame_3.Gestione_prenotazioni.enums.TipoPostazione;
import backend_esame_3.Gestione_prenotazioni.service.EdificioService;
import backend_esame_3.Gestione_prenotazioni.service.PostazioneService;
import backend_esame_3.Gestione_prenotazioni.service.PrenotazioneService;
import backend_esame_3.Gestione_prenotazioni.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
@PropertySource("application.properties")
public class Runner implements CommandLineRunner {

    @Autowired
    private EdificioService edificioService;


    @Autowired
    private PostazioneService postazioneService;
    @Autowired
    private PrenotazioneService prenotazioneService;
    @Autowired
    private UtenteService utenteService;



    @Override
    public void run(String... args) throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(GestionePrenotazioniApplication.class);

        Scanner scanner = new Scanner(System.in);


        // Richiesta dell'ID dell'utente
        System.out.println("Inserisci l'ID dell'utente:");
        Long utenteId = scanner.nextLong();
        Utente utente = utenteService.getUtente(utenteId).orElse(null);

        if (utente != null) {
            // Estrai la lista degli edifici
            List<Edificio> edifici = edificioService.getEdifici();

            // Estrai le città dai dati degli edifici
            List<String> cittaDisponibili = edifici.stream()
                    .map(Edificio::getCitta)
                    .distinct() // Rimuovi le città duplicate
                    .collect(Collectors.toList());

            // Visualizza le città disponibili
            System.out.println("Città disponibili:");
            for (int i = 0; i < cittaDisponibili.size(); i++) {
                String citta = cittaDisponibili.get(i);
                System.out.println(STR."\{i + 1}. \{citta}"); // ((i + 1) + ". " + citta)
            }

            // Richiesta della città desiderata
            System.out.println("Inserisci il numero corrispondente alla città desiderata:");
            int sceltaCitta = scanner.nextInt();
            // Verifica se l'input dell'utente è valido
            if (sceltaCitta >= 1 && sceltaCitta <= cittaDisponibili.size()) {
                String citta = cittaDisponibili.get(sceltaCitta - 1);

            // Recupera i tipi di postazione disponibili per la città selezionata
            List<TipoPostazione> tipiPostazioneDisponibili = postazioneService.getTipiPostazioneDisponibiliPerCitta(citta);

            // Visualizza i tipi di postazione disponibili
            System.out.println("Tipi di postazione disponibili per la città " + citta + ":");
            for (TipoPostazione tipo : tipiPostazioneDisponibili) {
                System.out.println("- " + tipo);
            }


            // Chiede il tipo di postazione desiderato
            System.out.println("Inserisci il tipo di postazione desiderato:");
            String tipoPostazioneString = scanner.next();
            TipoPostazione tipoPostazione = TipoPostazione.valueOf(tipoPostazioneString.toUpperCase());

            // Trova le postazioni disponibili in base alla città e al tipo desiderati
            LocalDate data = LocalDate.now();
            List<Postazione> postazioniDisponibili = postazioneService.trovaPostazioniPerTipoECitta(tipoPostazione, citta, data);

            if (!postazioniDisponibili.isEmpty()) {
                System.out.println("Postazioni disponibili:");
                for (Postazione postazione : postazioniDisponibili) {
                    System.out.println(postazione);
                }

                // Selezione della postazione
                System.out.println("Inserisci l'ID della postazione desiderata:");
                Long postazioneId = scanner.nextLong();
                Postazione postazioneSelezionata = postazioniDisponibili.stream()
                        .filter(p -> p.getId().equals(postazioneId))
                        .findFirst()
                        .orElse(null);

                if (postazioneSelezionata != null) {
                    boolean dataValida = false;
                    LocalDate dataPrenotazione = null;

                    while (!dataValida) {
                        System.out.println("Inserisci la data di prenotazione (AAAA-MM-GG):");
                        String dataPrenotazioneString = scanner.next();
                        dataPrenotazione = LocalDate.parse(dataPrenotazioneString);

                        List<Prenotazione> prenotazioniUtente = prenotazioneService.getPrenotazioniByDataAndUtente(dataPrenotazione, utente);

                        if (prenotazioniUtente.isEmpty()) {
                            dataValida = true;
                        } else {
                            System.out.println("Hai già una prenotazione per questa data. Inserisci un'altra data.");
                        }
                    }

                    // Effettua la prenotazione
                    prenotazioneService.prenotaPostazione(postazioneSelezionata, utente, dataPrenotazione);
                } else {
                    System.out.println("Postazione non valida.");
                }
            } else {
                System.out.println("Nessuna postazione disponibile in base alla selezione.");
            }
        } else {
            System.out.println("Utente non trovato.");
        }
    }
}
}




//        // Chiedi all'utente di inserire il suo username
//        System.out.println("Inserisci il tuo username:");
//        String username = scanner.nextLine();
//
//        // Ottieni l'utente dal servizio
//        Utente utente = utenteService.getUtenteByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
//
//        // Chiedi all'utente di inserire il tipo di postazione
//        System.out.println("Inserisci il tipo di postazione (PRIVATO, OPENSPACE o SALA_RIUNIONI):");
//        String tipoPostazioneStr = scanner.nextLine();
//        TipoPostazione tipoPostazione = TipoPostazione.valueOf(tipoPostazioneStr);
//
//        // Ottieni le città disponibili dalle informazioni sugli edifici
//        List<String> cittaDisponibili = edificioService.getCittaDisponibili();
//
//        // Mostra le città disponibili
//        System.out.println("Città disponibili:");
//        for (int i = 0; i < cittaDisponibili.size(); i++) {
//            System.out.println((i + 1) + ". " + cittaDisponibili.get(i));
//        }
//
//        // Chiedi all'utente di selezionare una città
//        System.out.println("Seleziona una città inserendo il numero corrispondente:");
//        int sceltaCitta = scanner.nextInt();
//        String cittaScelta = cittaDisponibili.get(sceltaCitta - 1);
//
//        // Ottieni la data odierna
//        LocalDate dataOdierna = LocalDate.now();
//
//        // Trova le postazioni disponibili per il tipo e la città specificati
//        List<Postazione> postazioniDisponibili = postazioneService.trovaPostazioniPerTipoECitta(tipoPostazione, cittaScelta, dataOdierna);
//
//        // Mostra i risultati
//        if (postazioniDisponibili.isEmpty()) {
//            System.out.println("Nessuna postazione disponibile per il tipo e la città specificati.");
//        } else {
//            System.out.println("Postazioni disponibili:");
//            for (Postazione postazione : postazioniDisponibili) {
//                System.out.println(postazione);
//            }
//        }
//
//    }
//
//}