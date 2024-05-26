package de.eldecker.dhbw.spring.badnews.helferlein;

import static org.springframework.data.domain.Sort.Direction.ASC;

import org.springframework.data.domain.Sort;


/**
 * Diese Klasse enthält Konstanten für die Sortier-Reihenfolge
 * bei Paginierung. 
 */
public class SortierReihenfolgeKonstanten {

    /**
     * Dummy-Konstruktor, um Instanziierung dieser Klasse zu verhindern.
     */
    private SortierReihenfolgeKonstanten() {}
    
    
    /** 
     * Sortier-Reihenfolge für Paginierung: 
     * Aufsteigend (Ascending) nach Attribut "id" (Primärschlüssel). 
     */
    public static final Sort SORT_ID_ASC = Sort.by( ASC, "id" );
    
}
