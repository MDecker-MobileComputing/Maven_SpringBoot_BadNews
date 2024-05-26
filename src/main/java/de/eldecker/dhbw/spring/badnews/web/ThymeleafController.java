package de.eldecker.dhbw.spring.badnews.web;

import static de.eldecker.dhbw.spring.badnews.helferlein.SortierReihenfolgeKonstanten.SORT_ID_ASC;
import static java.lang.String.format;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenEntity;
import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenRepo;
import de.eldecker.dhbw.spring.badnews.helferlein.SchlagzeilenException;
import de.eldecker.dhbw.spring.badnews.logik.PaginierungChecker;
import de.eldecker.dhbw.spring.badnews.model.AnzahlByKategorie;


/**
 * Controller-Klasse für Thymeleaf-Templates. Jede Mapping-Methode
 * gibt den String mit dem Namen der Template-Datei (ohne Datei-Endung)
 * zurück, die angezeigt werden soll.
 */
@Controller
@RequestMapping( "/app/" )
public class ThymeleafController {

    private final static Logger LOG = LoggerFactory.getLogger( ThymeleafController.class );

    /** Repo-Bean für Zugriff auf Tabelle mit Schlagzeilen. */
    private SchlagzeilenRepo _repo;
    
    /** Service-Bean für div. Checks im Zusammenhang mit der Paginierung. */
    private PaginierungChecker _checker;




    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public ThymeleafController( SchlagzeilenRepo repo,
                                PaginierungChecker checker) {
        _repo    = repo;
        _checker = checker;
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
     * @return Name der Template-Datei "schlagzeilen-fehler.html" ohne Datei-Endung. 
     */
    @ExceptionHandler(Exception.class)
    public String exceptionBehandeln( Exception exception, Model model ) {

        String fehlertext = "";

        if ( exception instanceof MethodArgumentTypeMismatchException ex ) {

            fehlertext = format( "Ungültiger Wert für URL-Parameter \"%s\" übergeben: \"%s\"",
                                 ex.getName(), ex.getValue() );
        } else {

            fehlertext = exception.getMessage();
        }

        LOG.error( fehlertext );
        model.addAttribute( "fehlermeldung", fehlertext );

        return "schlagzeilen-fehler";
    }


    /**
     * Eine Seite mit (einer Liste von) Schlagzeilen anzeigen.
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
     * @return Name der Template-Datei "schlagzeilen-liste.html" ohne Datei-Endung.
     * 
     * @throws SchlagzeilenException Ungültige {@code int]}-Werte für URL-Parameter übergeben, 
     *                               siehe {@link #exceptionBehandeln(Exception, Model)}
     *
     * @throws MethodArgumentTypeMismatchException Für URL-Parameter {@code seite} oder 
     *                                             {@code anzahl} übergebene Werte konnten 
     *                                             nicht nach {@code int} geparst werden                                             
     */
    @GetMapping( "/schlagzeilen" )
    public String liste( Model model,
                         @RequestParam( value = "seite" , required = false, defaultValue = "1"  ) int seite ,
                         @RequestParam( value = "anzahl", required = false, defaultValue = "10" ) int anzahl )
            throws SchlagzeilenException {

        _checker.checkeSeiteUndAnzahl( seite, anzahl ); // throws SchlagzeilenException


        final PageRequest seitenRequest = PageRequest.of( seite - 1, anzahl, SORT_ID_ASC );

        // *** eigentliche DB-Abfrage ***
        final Page<SchlagzeilenEntity> ergebnisPage = _repo.findAll( seitenRequest );

        _checker.checkErgebnisPage( ergebnisPage, seite );  // throws SchlagzeilenException

        final List<SchlagzeilenEntity> schlagzeilenListe = ergebnisPage.getContent();
        final int maxSeite = ergebnisPage.getTotalPages();

        model.addAttribute( "schlagzeilenliste", schlagzeilenListe );
        model.addAttribute( "seiteNr"          , seite             );
        model.addAttribute( "maxSeite"         , maxSeite          );
        model.addAttribute( "anzahl"           , anzahl            );

        return "schlagzeilen-liste";
    }


    /**
     * Einzelne Schlagzeile anzeigen.
     *
     * @param id ID (Primärschlüssel) der Schlagzeile, die angezeigt werden soll.
     *
     * @param model Objekt, in dem die Werte für die Platzhalter in der Template-Datei
     *              definiert werden.
     *
     * @return Name der Template-Datei "schlagzeile-einzeln.html" ohne Datei-Endung.
     * 
     * @throws SchlagzeilenException Keine Schlagzeile mit {@code id} gefunden
     */
    @GetMapping( "/schlagzeile/{id}" )
    public String schlagzeile( @PathVariable("id") Long id,
                               Model model ) throws SchlagzeilenException {

        final Optional<SchlagzeilenEntity> schlagzeileOptional = _repo.findById( id );
        if ( schlagzeileOptional.isEmpty() ) {
         
            final String text = format( "Keine Schlagzeile mit ID=%d gefunden.", id );
            throw new SchlagzeilenException( text );
        }        
        
        model.addAttribute( "schlagzeile", schlagzeileOptional.get() );

        return "schlagzeile-einzeln";
    }
    
    
    /**
     * Seite mit Statistik (Anzahl Inlands/Auslands-Schlagzeilen) anzeigen.
     * 
     * @param model Objekt, in dem die Werte für die Platzhalter in der Template-Datei
     *              definiert werden.
     *
     * @return Name der Template-Datei "statistik.html" ohne Datei-Endung.
     * 
     * @throws SchlagzeilenException Query hat mehr als zwei Ergebnisdatensätze
     *                               zurückgeliefert 
     */
    @GetMapping( "/statistik" )
    public String statistik( Model model ) throws SchlagzeilenException {
     
        final List<AnzahlByKategorie> anzByKategorieList = 
                                        _repo.zaehleSchlagzeilenInlandAusland();
        
        final int listSize = anzByKategorieList.size(); 
        if ( listSize > 2 ) {
            
            throw new SchlagzeilenException( 
                    "Mehr als zwei Einträge in Ergebnisliste für Statistik: " + 
                    listSize );
        }

        int summe = 0;
        for ( AnzahlByKategorie abk : anzByKategorieList ) {
            
            summe += abk.anzahl();
        }
        
        model.addAttribute( "statistikzeilen", anzByKategorieList );
        model.addAttribute( "summe"          , summe              );
        
        return "statistik";
    }                              

}
