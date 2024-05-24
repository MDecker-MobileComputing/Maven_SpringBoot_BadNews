package de.eldecker.dhbw.spring.badnews.helferlein;


/**
 * Eigene Exceptionklasse für die Anwendung.
 */
@SuppressWarnings("serial")
public class SchlagzeilenException extends Exception {

    /**
     * Konstruktor mit dem nur die Fehlermeldung übergeben
     * wird.
     * 
     * @param message Fehlermeldung
     */
    public SchlagzeilenException( String message ) {
        
        super ( message );
    }
    
    
    /**
     * Konstruktor um neben Fehlermeldung {@code messag} noch
     * zusätzlich auslösende Exception mitzugeben.
     *  
     * @param message Fehlermeldung
     * 
     * @param ex Auslösende Exception
     */
    public SchlagzeilenException( String message, Exception ex ) {
        
        super ( message, ex );
    }
    
}
