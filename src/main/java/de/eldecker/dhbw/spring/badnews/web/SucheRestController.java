package de.eldecker.dhbw.spring.badnews.web;

import static de.eldecker.dhbw.spring.badnews.helferlein.SortierReihenfolgeKonstanten.SORT_ID_ASC;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenEntity;
import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenRepo;
import de.eldecker.dhbw.spring.badnews.helferlein.SchlagzeilenException;
import de.eldecker.dhbw.spring.badnews.model.Schlagzeile;


/**
 * REST-Controller mit Endpunkt für die Suche nach Schlagzeilen.
 */
@RestController
@RequestMapping( "/api/v1" )
public class SucheRestController {
    
    private final static Logger LOG = LoggerFactory.getLogger( SucheRestController.class );
    
    
    /** Repo-Bean für Zugriff auf Tabelle mit Schlagzeilen. */
    private SchlagzeilenRepo _repo;
    
    
    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public SucheRestController( SchlagzeilenRepo repo ) {
        
        _repo = repo;
    }

    
    /**
     * Diese Methode behandelt Exceptions, die vom REST-Endpunkt
     * in dieser Klasse geworfen wurde. Es wird eine Fehlermeldung auf
     * den Logger geschrieben und die Fehlermeldung zusammen mit
     * HTTP-Status-Code 400 (Bad Request) als REST-Antwort zurückgegeben.
     * 
     * @param ex Exception, die bei Aufruf des REST-Endpunkt geworfen
     *           wurde
     * 
     * @return String mit Fehlermeldung, HTTP-Status-Code 400 (Bad Request)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionBehandeln( Exception ex ) {

        final String fehlerText = "Fehler bei Suchanfrage: " + ex.getMessage(); 
        LOG.error( fehlerText );
        
        return new ResponseEntity<>( fehlerText, BAD_REQUEST );
    }        
    
    
    /**
     * REST-Endpunkt für "Volltextsuche" von Schlagzeilen.
     * <br><br>
     * 
     * Im Wurzelverzeichnis des Projekts befindet sich eine
     * Datei mit einigen REST-Requests zum Test dieses Endpunkts
     * mit dem Browser-Plugin "Talend API Tester".
     * 
     * @param query Pflichtparameter mit Suchbegriff ; Leerzeichen am Anfang/Ende 
     *              werden entfernt, die Groß-/Kleinschreibung bei der Suche.
     *          
     * @param seite 1-basierte Nummer der Ergebnis-Seite (Default-Wert: 1);
     *              für max. Seitenzahl siehe Header-Feld "X-Anzahl-Seiten";
     *              es wird eine Exception geworfen, wenn Wert kleiner als 1
     *              ist.
     * 
     * @param anzahl Anzahl Treffer pro Seite; es wird eine Exception geworfen,
     *               wenn Wert kleiner als 1 ist.
     * 
     * @return Status-Code 200 wenn die Suche ausgeführt werden konnte
     *         (auch mit leerer Ergebnismenge); bei Fehler Status-Code
     *         400. Bei erfolgreicher Suche sind auch die von Methode
     *         {@link #erzeugeAntwortHeader(Page)} erzeugten HTTP-Header
     *         gesetzt.
     * 
     * @throws SchlagzeilenException Wenn Suchbegriff {@code q} weniger als  
     *                               drei Zeichen enthält (nach trimmen)
     */
    @GetMapping( "/suche" )
    public ResponseEntity<List<Schlagzeile>> suche( 
            @RequestParam( value = "query" , required = true                       ) String query, 
            @RequestParam( value = "seite" , required = false, defaultValue = "1"  ) int seite ,
            @RequestParam( value = "anzahl", required = false, defaultValue = "10" ) int anzahl )                                                                                                                                                                                                                                                                           
          throws SchlagzeilenException {
                     
        final String queryTrimmed = query.trim();
        
        if ( queryTrimmed.length() < 3 ) {
            
            throw new SchlagzeilenException( "Such-String muss mindestens drei Zeichen haben" );
        }
        
        final PageRequest pageRequest = PageRequest.of( seite - 1, anzahl, SORT_ID_ASC ); 
        
        final Page<SchlagzeilenEntity> ergebnisPage = 
                                 _repo.sucheSchlagzeilen( queryTrimmed, pageRequest );
                
        final List<SchlagzeilenEntity> dbErgebnisList = ergebnisPage.getContent();
                
        final List<Schlagzeile> ergebnisList = 
                dbErgebnisList.stream().map( entity -> {
            
                    final int    idInt = (int) entity.getId().intValue();
                    final String text  =       entity.getSchlagzeile();
            
                    return new Schlagzeile( idInt, text );
            
        }).toList();
        
        final HttpHeaders antwortHeader = erzeugeAntwortHeader( ergebnisPage );
        
        return new ResponseEntity<>( ergebnisList, antwortHeader, OK );
    }
    
    
    /**
     * Methode erzeugt HTTP-Header für Antwort REST-Methode {@link #suche(String, int, int)}.
     * <br><br>
     * 
     * Beispiel für erzeugte Header:
     * <pre>
     *   X-Anzahl-Treffer-Gesamt: 101
     *   X-Anzahl-Treffer-Seite: 50
     *   X-Anzahl-Seiten: 3
     * </pre>
     * 
     * @param page Objekt mit einer Ergebnis-Seite (ggf. Teilmenge der Treffer) und 
     *             Meta-Informationen wie höchste Seitennummer oder Gesamtanzahl
     *             der Treffer
     * 
     * @return HTTP-Header für Antwort an Client
     */
    private HttpHeaders erzeugeAntwortHeader( Page<SchlagzeilenEntity> page ) {

        final HttpHeaders antwortHeader = new HttpHeaders();
        
        antwortHeader.set( "X-Anzahl-Treffer-Gesamt", page.getTotalElements()    + "" );        
        antwortHeader.set( "X-Anzahl-Treffer-Seite" , page.getNumberOfElements() + "" );
        antwortHeader.set( "X-Anzahl-Seiten"        , page.getTotalPages()       + "" );

        return antwortHeader;
    }
    
}