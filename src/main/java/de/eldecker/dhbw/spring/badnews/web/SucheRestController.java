package de.eldecker.dhbw.spring.badnews.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    /**
     * Konfiguration der Paginierung für die Such-Query: Nur die erste Seite von 
     * Ergebnisssen (Seite mit Index 0) soll zurückgegeben werden, auf einer Seite
     * höchstens 50 Schlagzeilen.
     */
    private static final PageRequest PAGE_REQUEST_MAX_50_TREFFER = PageRequest.of( 0, 50 ); 

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
     * Diese Methode behandelt Exceptions, die von einem der REST-Endpunkte
     * in dieser Klasse geworfen wurde. Es wird eine Fehlermeldung auf
     * den Logger geschrieben und die Fehlermeldung zusammen mit
     * HTTP-Status-Code 400 (Bad Request) als REST-Antwort zurückgegeben.
     * 
     * @param ex Exception, die bei Aufruf eines REST-Endpunkts in 
     *           dieser Klasse geworfen wurde            
     * 
     * @return String mit Fehlermeldung, HTTP-Status 400 (Bad Request)
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
     * @param q Suchbegriff ("q" steht für "query"); Leerzeichen am
     *          Anfang/Ende werden entfernt, die Groß-/Kleinschreibung
     *          bei der Suche; Pflichtparameter!
     * 
     * @return Immer Status-Code 200 wenn Suche ausgeführt werden konnte
     *         (auch mit leerer Ergebnismenge); bei Fehler Status-Code
     *         400. Bei erfolgreicher Suche ist in HTTP-Status-Feld
     *         {@code X-Anzahl-Treffer} die Anzahl der Treffer enthalten.
     * 
     * @throws SchlagzeilenException Wenn Suchbegriff {@code q} weniger 
     *                               als drei Zeichen enthält. 
     */
    @GetMapping( "/suche" )
    public ResponseEntity<List<Schlagzeile>> suche( @RequestParam("q") String q )
            throws SchlagzeilenException {
                     
        final String qTrimmed = q.trim();
        
        if ( qTrimmed.length() < 3 ) {
            
            throw new SchlagzeilenException( "Suchstring muss mindestens drei Zeichen haben" );
        }
        
        final List<SchlagzeilenEntity> dbErgebnisList = 
                _repo.sucheSchlagzeilen( qTrimmed, PAGE_REQUEST_MAX_50_TREFFER );
        
        final List<Schlagzeile> ergebnisList = 
                dbErgebnisList.stream().map( entity -> {
            
                    final int    idInt = (int) entity.getId().intValue();
                    final String text  =       entity.getSchlagzeile();
            
                    return new Schlagzeile( idInt, text );
            
        }).toList();
        
        final int anzahlTreffer = ergebnisList.size();
        LOG.info( "Anzahl Treffer für Suchbegriff \"{}\" gefunden: {}", 
                  q, anzahlTreffer );
        
        final HttpHeaders antwortHeaders = new org.springframework.http.HttpHeaders();
        antwortHeaders.set( "X-Anzahl-Treffer", anzahlTreffer + "" );        
        
        return new ResponseEntity<>( ergebnisList, antwortHeaders, OK );
    }
    
}