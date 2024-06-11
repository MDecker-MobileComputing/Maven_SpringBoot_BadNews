package de.eldecker.dhbw.spring.badnews.db;

import static jakarta.persistence.GenerationType.SEQUENCE;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


/**
 * Ein Objekt dieser Entity-Klasse repräsentiert eine Tabellenzeile
 * mit der Schlagzeile einer schlechten Nachricht.
 */
@Entity
@Table( name = "Schlagzeilen" )
public class SchlagzeilenEntity {

    /**
     * Primärschlüssel, muss von uns nicht selbst befüllt werden, deshalb
     * gibt es auch keine Setter-Methode für dieses Attribut.
     * <br><br>
     *
     * Bei Wahl der Strategie {@code AUTO} wird auch {@code SEQUENCE}
     * gewählt.
     */
    @Id
    @GeneratedValue( strategy = SEQUENCE )
    private Long id;

    /** Text der zufällig erzeugten Negativschlagzeile. */
    private String schlagzeile;

    /** {@code true} wenn die Schlagzeile das Inland betrifft, sonst {@code false}. */
    private boolean inland;


    /**
     * Default-Konstruktor, wird von JPA benötigt.
     */
    public SchlagzeilenEntity() {

        schlagzeile = "";
    }


    /**
     * Convenience-Konstruktor für Inlands-Schlagzeile.
     *
     * @param schlagzeile Text der Inlands-Schlagzeile
     */
    public SchlagzeilenEntity( String schlagzeile ) {

        this.schlagzeile = schlagzeile;
        this.inland      = true;
    }


    /**
     * Konstruktor für neue Schlagzeile.
     *
     * @param schlagzeile Text der Schlagzeile
     *
     * @param inland {@code true} für Inlands-Schlagzeile, sonst {@code false}
     */
    public SchlagzeilenEntity( String schlagzeile, boolean inland ) {

        this.schlagzeile = schlagzeile;
        this.inland      = inland;
    }


    /**
     * Getter für Primärschlüssel der Entity, steht aber echt nach
     * Persistierung mit JPA zur Verfügung.
     *
     * @return Primärschlüssel/ID
     */
    public Long getId() {

        return id;
    }


    /**
     * Getter für den Text der Schlagzeile.
     *
     * @return Text der Schlagzeile.
     */
    public String getSchlagzeile() {

        return schlagzeile;
    }


    /**
     * Setter für den Text der Schlagzeile.
     *
     * @param schlagzeile Text der Schlagzeile.
     */
    public void setSchlagzeile( String schlagzeile ) {

        this.schlagzeile = schlagzeile;
    }


    /**
     * Getter für Abfrage, ob es sich um eine Inlands-Schlagzeile
     * handelt.
     *
     * @return {@code true} für Inlands-Schlagzeile, sonst {@code false};
     *         eine Inlands-Schlagzeile enthält einen Ort in Deutschland
     *         (Bundesland in Deutschland)
     */
    public boolean isInland() {

        return inland;
    }


    /**
     * Setter für Flag für Inlands-/Auslands-Nachrichten
     *
     * @param inland {@code true} für Inlands-Schlagzeile, sonst {@code false}
     */
    public void setInland( boolean inland ) {

        this.inland = inland;
    }


    /**
     * Methode liefert String-Repräsentation des Objekts zurück.
     *
     * @return String mit ID (wenn schon gesetzt) und Schlagzeile.
     *         Beispiel:
     *         {@code Schlagzeile ID=123 (Inland): Hungersnot in Baden-Württemberg }
     */
    @Override
    public String toString() {

        final String inlandAusland = inland ? "Inland" : "Ausland";

        return String.format( "Schlagzeile ID=%d (%s): %s",
                              getId(), inlandAusland, getSchlagzeile() );
    }


    /**
     * Methode gibt Hashwert (sollte für ein Objekt eindeutig sein) zurück.
     * Die ID geht nicht in die Berechnung des Hashwerts ein, weil sie evtl.
     * noch nicht gesetzt ist.
     *
     * @return Hashwert des Objekts
     */
    @Override
    public int hashCode() {

        return Objects.hash( schlagzeile, inland );
    }


    /**
     * Die ID wird bei dem Vergleich nicht berücksichtigt, weil sie evtl.
     * noch nicht gesetzt ist.
     *
     * @param obj Zu vergleichendes Objekt
     *
     * @return {@code true} gdw. {@obj} auch eine Instanz der Klasse
     *         {@link SchlagzeilenEntity} ist (oder eine Instanz einer
     *         Unterklasse dieser Klasse} und alle Attribute bis auf
     *         die ID denselben Wert haben.
     */
    @Override
    public boolean equals( Object obj ) {

        if ( obj == this ) { return true; }

        if ( obj == null ) { return false; }

        if ( obj instanceof SchlagzeilenEntity anderes ) {

            return Objects.equals( schlagzeile, anderes.schlagzeile ) &&
                   inland == anderes.inland;
        } else {

            return false;
        }
    }

}
