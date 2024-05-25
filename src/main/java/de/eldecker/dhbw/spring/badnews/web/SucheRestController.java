package de.eldecker.dhbw.spring.badnews.web;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
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
    
    /**
     * Konfiguration der Paginierung für die Such-Query: Nur die erste Seite von 
     * Ergebnisssen (Seite mit Index 0) soll zurückgegeben werden, auf einer Seite
     * höchstens 50 Schlagzeilen.
     */
    private static final PageRequest PAGE_REQUEST_MAX_50_TREFFER = PageRequest.of( 0, 50 ); 

    /** Repo-Bean für Zugriff auf Tabelle mit Schlagzeilen. */
    private SchlagzeilenRepo _repo;
    
    
    /**
     * Konstruktor für <i>Dependency Injection</i>:
     */
    @Autowired
    public SucheRestController( SchlagzeilenRepo repo ) {
        
        _repo = repo;
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
     *          bei der Suche 
     * 
     * @return Immer Status-Code 200 wenn Suche ausgeführt werden konnte
     *         (auch mit leerer Ergebnismenge)
     * 
     * @throws SchlagzeilenException Wenn Suchbegriff {@code q} weniger 
     *         als drei Zeichen enthält. 
     */
    @GetMapping( "/suche" )
    public ResponseEntity<List<Schlagzeile>> suche( @RequestParam("q") String q )
            throws SchlagzeilenException {
                     
        final String qTrimmed = q.trim();
        
        if ( qTrimmed.length() < 3 ) {
            
            throw new SchlagzeilenException( "Suchstring muss mindestens drei Zeichen haben" );
        }
        
        final List<SchlagzeilenEntity> dbErgebnisList = 
                _repo.sucheSchlagzeilen( q.trim(), PAGE_REQUEST_MAX_50_TREFFER );
        
        final List<Schlagzeile> ergebnisList = 
                dbErgebnisList.stream().map( entity -> {
            
                    final int    idInt = (int) entity.getId().intValue();
                    final String text  =       entity.getSchlagzeile();
            
                    return new Schlagzeile( idInt, text );
            
        }).collect( toList() );
        
        return ResponseEntity.ok( ergebnisList );
    }
    
}