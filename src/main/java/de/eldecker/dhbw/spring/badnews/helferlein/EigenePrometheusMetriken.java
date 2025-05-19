package de.eldecker.dhbw.spring.badnews.helferlein;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.eldecker.dhbw.spring.badnews.web.SucheRestController;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;


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
public class EigenePrometheusMetriken {

    /** 
     * Zähler für Metrik mit Gesamtanzahl der Suchvorgänge, siehe {@link SucheRestController}.
     * Technischer Name der Metrik (z.B. für PromQL-Abfrage): 
     * {@code badnews_suchvorgaenge_total} 
     * 
     * <br>
     * PomQL-Query für Anzahl Suchvorgänge in den letzten 5 Minuten:
     * {@code increase(badnews_suchvorgaenge_total[5m])}
     */ 
    private final Counter _counterSuchvorgaenge;
    
    /**
     * Timer zur Messung Suchdauer.
     */
    private final Timer _timerSuchvorgaenge;
    
    
    /**
     * Konstruktor für Erzeugung der {@code Meter}-Objekte.
     */
    @Autowired
    public EigenePrometheusMetriken( MeterRegistry meterRegistry ) {
        
        _counterSuchvorgaenge = 
                Counter.builder( "badnews_suchvorgaenge" )
                       .description( "Anzahl der Suchvorgänge (egal ob erfolgreich oder nicht" )
                       .tags( "umgebung", "development",  
                              "funktion", "suche" ) // Tags: Key-Value-Paare
                       .register( meterRegistry );
      
        _timerSuchvorgaenge = 
        		Timer.builder ( "badnews_suchdauer" )
        		     .description( "Dauer Suchvorgänge" )
                     .tags( "umgebung", "development",  
                            "funktion", "suche" )
                     .register( meterRegistry );        
    }
    
    
    /**
     * Methode wird aufgerufen, wenn ein Suchvorgang durchgeführt wird.
     * Es ist egal, ob der Suchvorgang erfolgreich ist oder nicht. 
     */
    public void erhoeheAnzahlSuchvorgaenge() {
        
        _counterSuchvorgaenge.increment();
    }
    
    
    /**
     * Getter für Timer für Zeitmessung Suchdauer.
     * 
     * @return Timer, mit dem Zeitmessung für Suchvorgang durchgeführt werden kann.
     */
    public Timer getTimerFuerSuchvorgang() {
    	
    	return _timerSuchvorgaenge;
    }
    
}
