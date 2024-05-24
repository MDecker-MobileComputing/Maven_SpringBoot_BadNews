package de.eldecker.dhbw.spring.badnews.logik;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenEntity;


/**
 * Service-Bean, die zufällige (Negativ-)Schlagzeilen erzeugt.
 */
@Service
public class SchlagzeilenErzeuger {
    
    private final static Logger LOG = LoggerFactory.getLogger( SchlagzeilenErzeuger.class );

    /** Array mit negativen Ereignissen. */
    public static final String[] EREIGNISSE_ARRAY = {
            "Altersarmut", "Amoklauf", "Ausgangs-Sperre", "Ärztemangel", "Ausschreitungen",
            "Bankrott", "Bildungsnotstand", "Busunfall", "Brandstiftung", "Chemie-Unfall",
            "Cyberangriff", "Doping-Skandal", "Drogenkriminalität", "Dürre", "Entführung",
            "Erdbeben", "Erdrutsch", "Erpressung", "Explosion", "Finanzkrise", "Gasexplosion",
            "Gewaltserie", "Geflügerpest", "Großbrand", "Großschadenslage", "Hausbesetzung",
            "Handwerkermangel", "Hitzewelle", "Korruption", "Lawine", "Lebensmittelskandal",
            "Lehrermangel", "Massenkarambolage", "Massenpanik", "Mord", "Ölkatastrophe",
            "Regierungskrise", "Rinderwahn", "Rohstoffknappheit", "Schiffskollision", "Skandal",
            "Smog-Alarm", "Studierendenproteste", "Stromausfall", "Tierseuche", "Unwetter",
            "Überfall", "Überschwemmung", "Wahlmanipulation", "Waldbrand", "Waldsterben",
            "Wirtschaftskrise", "Vulkanausbruch"
        };
    
    /** Array mit Orten in Deutschland (zuerst die Bundesländer, dann noch einige Regionen). */
    public static final String[] ORTE_DEUTSCHLAD_ARRAY = {
            "Baden-Württemberg", "Bayern", "Bremen", "Berlin", "Brandenburg",
            "Hamburg", "Hessen", "Mecklenburg-Vorpommern", "Niedersachsen",
            "Nordrhein-Westfalen", "Rheinland-Pfalz", "Saarland", "Sachsen",
            "Sachsen-Anhalt", "Schleswig-Holstein", "Thüringen",
            
            // ab jetzt Regionen
            "Breisgau", "Franken", "Nordseeküste", "Ostseeküste", "Ostfriesland", "Schwarzwald" 
        };    

    /** Array mit Ländern (internationale Staaten). */
    public static final String[] ORTE_NATIONEN_ARRAY = {
            "Albanien", "Amerika", "Andorra", "Argentinien", "Armenien", "Australien",
            "Belgien", "Bosnien und Herzegowina", "Bulgarien", "China",
            "Dänemark", "Estland", "Finnland", "Frankreich", "Griechenland",
            "Irland", "Island", "Italien", "Japan", "Kanada", "Kosovo", "Kolumbien",
            "Kroatien", "Lettland", "Liechtenstein", "Litauen", "Luxemburg",
            "Malta", "Mazedonien", "Moldawien", "Monaco", "Montenegro",
            "Niederlande", "Norwegen", "Österreich", "Panama", "Polen",
            "Portugal", "Rumänien", "Russland", "San Marino", "Schweden",
            "Schweiz", "Serbien", "Singapur", "Slowakei", "Slowenien",
            "Spanien", "Taiwan", "Tschechien", "Türkei", "Ukraine",
            "Ungarn", "Vatikanstadt", "Vereinigtes Königreich", "Weißrussland"
        };    
     
    
    /** Zufallsgenerator */
    private final static Random _random = new Random();
    
    
    /**
     * Methode gibt ein zufällig ausgewähltes Element aus dem als
     * Argument übergebenen String-Array zurück.
     * 
     * @param stringArray Array mit Strings, von dem einer zufällig
     *                    ausgewählt werden soll
     *                    
     * @return Zufällig ausgewählter String aus {@code stringArray};
     *         leerer String, wenn {@code stringArray.length == 0}.
     */
    private String getZufallsElement(String[] stringArray) {
                
        final int laenge = stringArray.length;
        
        if ( laenge == 0) {
            
            LOG.warn( "Soll zufälligen String aus Array mit 0 Elementen zurückgeben." );
            return "";
        }
        
        int randomIndex = _random.nextInt( laenge );
        
        return stringArray[ randomIndex ];
    }
    
    
    /**
     * Methode erzeugt eine zufällige Schlagzeile der Form "EREIGNIS in ORT",
     * z.B. "Smog-Alarm in Estland".
     * 
     * @return Zufällig erzeugte Schlagzeile; je nach im Text enthaltenen
     *         Ort ist das Flag {@code inland} entsprechend gesetzt.
     */
    public SchlagzeilenEntity erzeugeZufallsSchlagzeile() {
        
        final String ereignis = getZufallsElement( EREIGNISSE_ARRAY );
        
        String ort = "";
        boolean inland = false;
        if ( _random.nextFloat() < .3 ) {
            
            ort = getZufallsElement( ORTE_DEUTSCHLAD_ARRAY );
            inland = true;
            
        } else {
            
            ort = getZufallsElement( ORTE_NATIONEN_ARRAY );
        }
        
        final String schlagzeile = String.format( "%s in %s", ereignis, ort ); 
        
        final SchlagzeilenEntity ergebnisEntity = 
                new SchlagzeilenEntity( schlagzeile, inland );
        
        return ergebnisEntity;
    }
    
    
    /**
     * Convenience-Methode, die {@code anzahl} zufällige Schlagzeilen auf einmal
     * erzeugt, siehe auch {@link #getZufallsSchlagzeile()}.
     * 
     * @param anzahl Anzahl der zu erzeugenden Schlagzeilen, muss echt-größer 0 sein.
     * 
     * @return Liste mit Schlagzeilen, kann leer sein aber nicht {@code null}
     */
    public List<SchlagzeilenEntity> erzeugetZufallsSchlagzeilen( int anzahl ) {
        
        if ( anzahl < 1 ) {
            
            LOG.warn( "Erzeugung von {} Schlagzeilen angefordert.", anzahl );
            return emptyList();
        }
        
        final List<SchlagzeilenEntity> ergebnisListe = new ArrayList<>( anzahl );
        
        final SchlagzeilenEntity[] ergebnisArray = new SchlagzeilenEntity[ anzahl ];
        
        for ( int i = 0 ; i < ergebnisArray.length; i++ ) {
            
            final SchlagzeilenEntity entity = erzeugeZufallsSchlagzeile();
            ergebnisListe.add( entity );
        }
        
        return ergebnisListe;
    }
    
}