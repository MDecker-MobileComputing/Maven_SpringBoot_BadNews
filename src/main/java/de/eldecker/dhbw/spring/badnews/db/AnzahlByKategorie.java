package de.eldecker.dhbw.spring.badnews.db;

/**
 * Record-Klasse für Ergebnis der Query-Methode
 * {@link SchlagzeilenRepo#countByInlandGrouped()}.
 */
public record AnzahlByKategorie( boolean inland, 
                                 long    anzahl ) {
}
