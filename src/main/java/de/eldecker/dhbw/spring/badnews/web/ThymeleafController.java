package de.eldecker.dhbw.spring.badnews.web;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static java.lang.String.format;
import static java.util.Locale.GERMANY;
import static java.text.NumberFormat.getNumberInstance;

import java.text.NumberFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenEntity;
import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenRepo;
import de.eldecker.dhbw.spring.badnews.helferlein.SchlagzeilenException;


/**
 * Controller-Klasse für Thymeleaf-Templates. Jetzt Mapping-Methode
 * gibt den String mit dem Namen der Template-Datei (ohne Datei-Endung)
 * zurück, die angezeigt werden soll.
 */
@Controller
@RequestMapping( "/app/" )
public class ThymeleafController {

    private final static Logger LOG = LoggerFactory.getLogger( ThymeleafController.class );

    /** Repo-Bean für Zugriff auf Tabelle mit Schlagzeilen. */
    private SchlagzeilenRepo _repo;

    /** Sortier-Reihenfolge für Paginierung: Aufsteigend nach Feld "id". */
    private static final Sort SORT_ID_ASC = Sort.by( ASC, "id" );

    /** Formatierer für Seitenzahlen (Tausendertrennpunkte einfügen). */
    private static final NumberFormat ZAHLENFORMATIERER = getNumberInstance( GERMANY );

    
    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public ThymeleafController( SchlagzeilenRepo repo ) {

        _repo = repo;
    }


    /**
     * Wenn eine Mapping-Methode eine Exception wirft, dann wird dieser
     * von der folgenden Methode behandelt und auf die Fehlerseite
     * weitergeleitet. Die Message aus {@code exception} wird auf den
     * Logger geschrieben und auch auf einer Fehlerseite angezeigt.
     *
     * @param exception Exception-Objekt, Message wird ausgelesen;
     *                  wenn Instanz von 
     *                  {@code MethodArgumentTypeMismatchException},
     *                  dann spezielle Fehlermeldung für ungültigen
     *                  {@code int}-Wert.
     *
     * @param model Objekt, in dem die Werte für die Platzhalter in der
     *              Template-Datei definiert werden; es wird die Message
     *              von {@code exception} in den Platzhalter "fehlermeldung"
     *              kopiert.
     *
     * @return Template-Datei "schlagzeilen-fehler"
     */
    @ExceptionHandler(Exception.class)
    public String exceptionBehandeln( Exception exception, Model model ) {

        String fehlertext = "";

        if ( exception instanceof MethodArgumentTypeMismatchException ex ) {

            fehlertext = format( "Ungültiger Wert für URL-Parameter \"%s\" übergeben.",
                                 ex.getName() );
        } else {

            fehlertext = exception.getMessage();
        }

        LOG.error( fehlertext );
        model.addAttribute( "fehlermeldung", fehlertext );

        return "schlagzeilen-fehler";
    }


    /**
     * Eine Seite mit Schlagzeilen anzeigen.
     *
     * @param model Objekt, in dem die Werte für die Platzhalter in der Template-Datei
     *              definiert werden.
     *
     * @param seite Optionaler URL-Parameter für die Seitennummer, 1-basiert;
     *              Default-Wert: 1, darf nicht 0 oder negativ sein.
     *
     * @param anzahl Optionaler URL-Parameter für Anzahl Schlagzeilen auf einer Seite;
     *               Default-Wert: 10; zulässiger Bereich 1 bis 500.
     *
     * @return Name Template-Datei "schlagzeilen-erfolg"
     *
     * @throws SchlagzeilenException Ungültige {@code int]}-Werte für URL-Parameter übergeben, siehe
     *                               {@link #exceptionBehandeln(Exception, Model)}
     *                               
     * @throws MethodArgumentTypeMismatchException Für URL-Parameter {@code seite} oder {@code anzahl}
     *                                             übergebene Werte konnten nicht nach {@code int} geparst
     *                                             werden                         
     */
    @GetMapping( "/schlagzeilen" )
    public String schlagzeilenAnzeigen( Model model,
                                        @RequestParam( value = "seite" , required = false, defaultValue = "1"  ) int seite ,
                                        @RequestParam( value = "anzahl", required = false, defaultValue = "10" ) int anzahl )
            throws SchlagzeilenException {

        checkeSeiteUndAnzahl( seite, anzahl ); // throws SchlagzeilenException


        final PageRequest seitenRequest = PageRequest.of( seite - 1, anzahl, SORT_ID_ASC );

        // *** eigentliche DB-Abfrage ***
        final Page<SchlagzeilenEntity> ergebnisPage = _repo.findAll( seitenRequest );

        checkErgebnisPage( ergebnisPage, seite );  // throws SchlagzeilenException
        
        final List<SchlagzeilenEntity> schlagzeilenListe = ergebnisPage.getContent();
        
        
        
        final int maxSeite = ergebnisPage.getTotalPages();
        if ( seite > maxSeite ) {
            
            throw new SchlagzeilenException( "Seite zu groß" );
        }
        


        model.addAttribute( "schlagzeilenliste", schlagzeilenListe );

        return "schlagzeilen-erfolg";
    }        


    /**
     * Überprüfung von Query zurückgegebener Seite.
     * 
     * @param ergebnisPage von Query zurückgelieferte Seite
     * 
     * @param seite von Nutzer als URL-Parameter übergebene Seiten-Nr; es
     *              wird überprüft, ob größer als letzte Seite 
     * 
     * @throws SchlagzeilenException Wenn Wert von {@code seite} zu groß oder 
     *                               {@code ergebnisPage} eine leere Liste von 
     *                               Schlagzeilen enthält.
     */
    private void checkErgebnisPage( Page<SchlagzeilenEntity> ergebnisPage, int seite ) 
            throws SchlagzeilenException  {
        
        final int nrLetzteSeite = ergebnisPage.getTotalPages();        
        if ( seite > nrLetzteSeite ) {
                        
            final String formattedSeite         = ZAHLENFORMATIERER.format( seite         );
            final String formattedNrLetzteSeite = ZAHLENFORMATIERER.format( nrLetzteSeite );            
            
            final String fehlertext = 
                    format( "Seite Nr. %s angefordert, aber letzte Seite ist %s.",
                            formattedSeite, formattedNrLetzteSeite );
            
            throw new SchlagzeilenException( fehlertext );
        }
        
        if ( ergebnisPage.getContent().isEmpty() ) {

            throw new SchlagzeilenException( "Leere Liste mit Schlagzeilen bekommen." );
        }
    }
    
    
    /**
     * Diese Methode überprüft die als URL-Parameter übergebenen Werte für
     * die Seite und die Anzahl der Schlagzeile pro Seite.
     * Wenn mindestens einer dieser beiden Werte nicht im gültigen
     * Bereich liegt, dann wird eine {@code SchlagzeilenException}
     * geworfen.
     * <br><br>
     * 
     * Siehe Doku zu Methode {@link #schlagzeilenAnzeigen(Model, int, int)}
     * für zulässige Werte für diese beiden URL-Parameter.
     *
     * @param seite Seite (1-basiert), als URL-Parameter-Wert erhalten
     *
     * @param anzahl Anzahl Schlagzeilen auf einer Seite, als URL-Parameter-Wert
     *               erhalten
     *
     * @throws SchlagzeilenException Exception-Objekt, das eine Fehlerbeschreibung
     *                               als "message" enthält.
     */
    private void checkeSeiteUndAnzahl( int seite, int anzahl ) throws SchlagzeilenException {

        if ( seite < 1 ) {

            final String fehlerText = 
                    format( "Ungültige Seite %d als URL-Parameter übergeben.", seite );

            throw new SchlagzeilenException( fehlerText );
        }

        if ( anzahl < 1 || anzahl > 500 ) {

            final String fehlerText = 
                    format( "Ungültige Wert %d für Anzahl Schlagzeile pro Seite übergeben.", anzahl );

            throw new SchlagzeilenException( fehlerText );
        }
    }

}
