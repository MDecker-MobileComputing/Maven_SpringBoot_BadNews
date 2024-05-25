package de.eldecker.dhbw.spring.badnews.model;

import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenRepo;


/**
 * Record-Klasse für Ergebnis der Query-Methode
 * {@link SchlagzeilenRepo#zaehleSchlagzeilenInlandAusland()}.
 * 
 * @param inland {@code true} wenn {@code anzahl} sich auf Inlands-Schlagzeilen bezieht,
 *               {@code false} wenn {@code anzahl} sich auf Auslands-Schlagzeilen bezieht.
 *               
 * @param anzahl Anzahl der Schlagzeilen
 */
public record AnzahlByKategorie( boolean inland,
                                 long    anzahl ) {
}
