package de.eldecker.dhbw.spring.badnews.logik;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenEntity;
import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenRepo;
import de.eldecker.dhbw.spring.badnews.helferlein.EigenePrometheusMetriken;


/**
 * Wenn die Tabelle mit den Schlagzeilen leer ist, dann sorgt die
 * Bean dieser Klasse dafür unmittelbar nach dem Start der Anwendung
 * dafür, dass eine bestimmte Anzahl an Schlagzeilen erzeugt wird.
 */
@Component
public class DatenImporterApplicationRunner implements ApplicationRunner {

    private final static Logger LOG = LoggerFactory.getLogger( DatenImporterApplicationRunner.class );

    /** Anzahl der Schlagzeilen die in eine ganz leere Schlagzeilentabelle einzufügen ist. */
    public static final int ANZAHL_SCHLAGZEILEN = 5_000;

    /** Repo-Bean für Zugriff auf Tabelle mit den Schlagzeilen. */
    private SchlagzeilenRepo _schlagzeilenRepo;

    /** Service-Bean zur Erzeugung von zufälligen Negativschlagzeilen. */
    private SchlagzeilenErzeuger _schlagzeilenErzeuger;
    
    /** Bean für Messung Dauer für Datenerzeugung. */
    private EigenePrometheusMetriken _prometheusMetriken;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    public DatenImporterApplicationRunner( SchlagzeilenRepo         schlagzeilenRepo,
                                           SchlagzeilenErzeuger     schlagzeilenErzeuger,
                                           EigenePrometheusMetriken prometheusMetriken 
                                         ) {

        _schlagzeilenRepo     = schlagzeilenRepo;
        _schlagzeilenErzeuger = schlagzeilenErzeuger;
        _prometheusMetriken   = prometheusMetriken;
    }


    /**
     * Diese Methode wird unmittelbar nach dem Start der Anwendung
     * ausgeführt.
     *
     * @param args Kommandozeilenargumente, werden nicht ausgewertet
     *
     * @throws Exception Wird nicht geworfen
     */
    @Override
    public void run( ApplicationArguments args ) throws Exception {

        final int anzahlSchlagzeilen = (int) _schlagzeilenRepo.count(); // Methode aus Interface CrudRepository
        if ( anzahlSchlagzeilen > 0 ) {

            LOG.info( "Datenbank enthält schon {} Schlagzeilen, es werden keine neuen hinzugefügt.",
                      anzahlSchlagzeilen );
        } else {

            LOG.warn( "Datenbank enthält überhaupt keine Schlagzeilen, werde {} Schlagzeilen erzeugen.",
                      ANZAHL_SCHLAGZEILEN );

            _prometheusMetriken.getTimerFuerDatenerzeugung().record(
            		
            		() -> {
            			
                        final List<SchlagzeilenEntity> schlagzeilenListe =
                                _schlagzeilenErzeuger.erzeugeZufallsSchlagzeilen( ANZAHL_SCHLAGZEILEN );

                        _schlagzeilenRepo.saveAll( schlagzeilenListe ); // Batch-Operation

                        LOG.warn( "{} zufällige Schlagzeilen erzeugt und in DB gespeichert.", 
                        		  ANZAHL_SCHLAGZEILEN );             			
            		}
            );            
        }
    }

}
