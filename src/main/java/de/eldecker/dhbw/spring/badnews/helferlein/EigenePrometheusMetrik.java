package de.eldecker.dhbw.spring.badnews.helferlein;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.eldecker.dhbw.spring.badnews.web.SucheRestController;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;


/**
 * Bean, um eigene Pull-Metrik über Micrometer für Prometheus bereitstellen.
 * <br><br>
 * 
 * Für die hier definierte Metrik werden folgende Zeilen auf die Textseite,
 * die von Prometheus regelmäßig abgefragt wird, geschrieben:
 * <pre>
 * # HELP badnews_suchvorgaenge_total Anzahl der Suchvorgänge
 * # TYPE badnews_suchvorgaenge_total counter
 * badnews_suchvorgaenge_total{funktion="suche",umgebung="development"} 2.0
 * </pre> 
 */
@Component
public class EigenePrometheusMetrik {

    /** 
     * Metrik mit Gesamtanzahl der Suchvorgänge, siehe {@link SucheRestController}.
     * Technischer Name der Metrik (z.B. für PromQL-Abfrage): 
     * {@code badnews_suchvorgaenge_total} 
     */ 
    private final Counter _zaehlerSuchvorgaenge;
    
    
    /**
     * Konstruktor für Erzeugung Counter-Objekt.
     */
    @Autowired
    public EigenePrometheusMetrik( MeterRegistry meterRegistry ) {
        
        _zaehlerSuchvorgaenge = 
                Counter.builder( "badnews_suchvorgaenge" )
                       .description( "Anzahl der Suchvorgänge" )
                       .tags( "umgebung", "development",  
                              "funktion", "suche" ) // Tags: Key-Value-Paare
                       .register( meterRegistry );
    }
    
    
    /**
     * Methode wird aufgerufen, wenn ein Suchvorgang durchgeführt wird.
     * Es ist egal, ob der Suchvorgang erfolgreich ist oder nicht. 
     */
    public void erhoeheAnzahlSuchvorgaenge() {
        
        _zaehlerSuchvorgaenge.increment();
    }
    
}
