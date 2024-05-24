package de.eldecker.dhbw.spring.badnews.logik;

import static java.lang.String.format;
import static java.text.NumberFormat.getNumberInstance;
import static java.util.Locale.GERMANY;

import java.text.NumberFormat;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenEntity;
import de.eldecker.dhbw.spring.badnews.helferlein.SchlagzeilenException;


/**
 * Diese Bean enthält Methoden zur Überprüfung von Request-Parametern
 * im Zusammenhang mit der Paginierung der Schlagzeilen.
 */
@Service
public class PaginierungChecker {

    /** Formatierer für Seitenzahlen (Tausendertrennpunkte einfügen). */
    private static final NumberFormat ZAHLENFORMATIERER = getNumberInstance( GERMANY );

    
    /**
     * Überprüfung von Query zurückgegebener Seite.
     *
     * @param ergebnisPage von Query zurückgelieferte Seite
     *
     * @param seite von Nutzer als URL-Parameter übergebene Seiten-Nr; es
     *              wird überprüft, ob größer als letzte Seite
     *
     * @throws SchlagzeilenException Wenn Wert von {@code seite} zu groß oder
     *                               {@code ergebnisPage} eine leere Liste von
     *                               Schlagzeilen enthält.
     */
    public void checkErgebnisPage( Page<SchlagzeilenEntity> ergebnisPage, int seite )
            throws SchlagzeilenException  {

        final int nrLetzteSeite = ergebnisPage.getTotalPages();
        if ( seite > nrLetzteSeite ) {

            final String formattedSeite         = ZAHLENFORMATIERER.format( seite         );
            final String formattedNrLetzteSeite = ZAHLENFORMATIERER.format( nrLetzteSeite );

            final String fehlertext =
                    format( "Seite Nr. %s angefordert, aber letzte Seite ist %s.",
                            formattedSeite, formattedNrLetzteSeite );

            throw new SchlagzeilenException( fehlertext );
        }

        if ( ergebnisPage.getContent().isEmpty() ) {

            throw new SchlagzeilenException( "Leere Liste mit Schlagzeilen bekommen." );
        }
    }


    /**
     * Diese Methode überprüft die als URL-Parameter übergebenen Werte für
     * die Seite und die Anzahl der Schlagzeile pro Seite.
     * Wenn mindestens einer dieser beiden Werte nicht im gültigen
     * Bereich liegt, dann wird eine {@code SchlagzeilenException}
     * geworfen.
     * <br><br>
     *
     * Siehe Doku zu Methode {@link #liste(Model, int, int)}
     * für zulässige Werte für diese beiden URL-Parameter.
     *
     * @param seite Seite (1-basiert), als URL-Parameter-Wert erhalten
     *
     * @param anzahl Anzahl Schlagzeilen auf einer Seite, als URL-Parameter-Wert
     *               erhalten
     *
     * @throws SchlagzeilenException Exception-Objekt, das eine Fehlerbeschreibung
     *                               als "message" enthält.
     */
    public void checkeSeiteUndAnzahl( int seite, int anzahl ) throws SchlagzeilenException {

        if ( seite < 1 ) {

            final String fehlerText =
                    format( "Ungültige Seite %d als URL-Parameter übergeben.", seite );

            throw new SchlagzeilenException( fehlerText );
        }

        if ( anzahl < 1 || anzahl > 500 ) {

            final String fehlerText =
                    format( "Ungültige Wert %d für Anzahl Schlagzeile pro Seite übergeben.", anzahl );

            throw new SchlagzeilenException( fehlerText );
        }
    }
    
}
