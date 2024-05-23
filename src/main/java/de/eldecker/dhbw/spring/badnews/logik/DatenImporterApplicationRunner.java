package de.eldecker.dhbw.spring.badnews.logik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenRepo;


/**
 * Wenn die Tabelle mit den Schlagzeilen leer ist, dann sorgt die
 * Bean dieser Klasse dafür unmittelbar nach dem Start der Anwendung
 * dafür, dass eine bestimmte Anzahl an Schlagzeilen erzeugt wird. 
 */
@Component
public class DatenImporterApplicationRunner implements ApplicationRunner {
    
    private final static Logger LOG = LoggerFactory.getLogger( DatenImporterApplicationRunner.class ); 

    /** Anzahl der Schlagzeilen die in eine ganz leere Schlagzeilentabelle einzufügen ist. */
    public static final int ANZAHL_SCHLAGZEILEN = 1_000;
    
    /** Repo-Bean für Zugriff auf Tabelle mit den Schlagzeilen. */
    private SchlagzeilenRepo _schlagzeilenRepo;

    
    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    public DatenImporterApplicationRunner( SchlagzeilenRepo  schlagzeilenRepo ) {
    
        _schlagzeilenRepo = schlagzeilenRepo;
    }
    
    
    /**
     * Diese Methode wird unmittelbar nach dem Start der Anwendung
     * ausgeführt.
     * 
     * @param args Kommandozeilenargumente, werden nicht ausgewertet
     */
    @Override    
    public void run( ApplicationArguments args ) throws Exception {      
        
        final int anzahlSchlagzeilen = (int) _schlagzeilenRepo.count(); // methode aus Interface CrudRepository
        if ( anzahlSchlagzeilen > 0) {
            
            LOG.info( "Datenbank enthält schon {} Schlagzeilen, es werden keine neuen hinzugefügt.", 
                      anzahlSchlagzeilen );             
        } else {
            
            LOG.warn( "Datenbank enthält überhaupt keine Schlagzeilen, werde {} Schlagzeilen erzeugen.", 
                      ANZAHL_SCHLAGZEILEN );                         
        }
    }    
    
}
