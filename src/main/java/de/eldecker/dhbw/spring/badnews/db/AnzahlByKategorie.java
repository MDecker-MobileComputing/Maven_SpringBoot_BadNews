package de.eldecker.dhbw.spring.badnews.db;

/**
 * Record-Klasse für Ergebnis der Query-Methode
 * {@link SchlagzeilenRepo#zaehleSchlagzeilenInlandAusland()}.
 */
public record AnzahlByKategorie( boolean inland,
                                 long    anzahl ) {
}
