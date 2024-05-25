package de.eldecker.dhbw.spring.badnews.model;


/**
 * Objekte dieser Record-Klasse werden als REST-Antwort nach JSON serialisiert.
 * 
 * @param id Nummer/Primärschlüssel der Schlagzeile
 * 
 * @param schlagzeile Text der Schlagzeile
 */
public record Schlagzeile ( int    id,
                            String schlagzeile ) {
}
