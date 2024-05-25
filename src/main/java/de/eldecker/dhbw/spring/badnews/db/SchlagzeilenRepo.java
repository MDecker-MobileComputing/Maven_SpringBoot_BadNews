package de.eldecker.dhbw.spring.badnews.db;

import de.eldecker.dhbw.spring.badnews.model.AnzahlByKategorie;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * In diesem Repository-Interface müssen wir nur abstrakte Methoden
 * definieren, die Implementierungen erzeugt <i>Spring Data JPA</i>
 * anhand der Methodennamen (Derived Query) oder einer über
 * Annotation definieren JPQL-Anweisung selbst hinzu.
 * <br><br>
 *
 * Da für die Anwendung eine Migration auf eine NoSQL-Datenbank
 * sehr unwahrscheinlich ist, können wir als Super-Interface
 * {@code JpaRepository} verwenden.
 */
public interface SchlagzeilenRepo extends JpaRepository<SchlagzeilenEntity, Long> {

    /**
     * Query-Methode mit JPQL-Query zum Zählen der Anzahl der 
     * Inlands- und Auslands-Nachrichten.
     * 
     * @return Liste mit zwei Element (je einen mit {@code inland=true} 
     *         und für {@inland=false}
     */
    @Query( "SELECT new de.eldecker.dhbw.spring.badnews.model.AnzahlByKategorie( s.inland, COUNT(s) ) " +
            "FROM SchlagzeilenEntity s " +
            "GROUP BY s.inland" )
    List<AnzahlByKategorie> zaehleSchlagzeilenInlandAusland();
    
    
    /**
     * Textsuche nach Schlagzeilen. 
     * 
     * @param suchstring Teilstring, der in Schlagzeilen enthalten sein muss
     *                   (Groß-/Kleinschreibung wird ignoriert)
     * 
     * @param pageable Objekt zur Steuerung der Paginierung (Anzahl Datensätze
     *                 pro Seite und Nummer der Seite)
     * 
     * @return Liste von gefundenen Schlagzeilen
     */
    @Query("SELECT s FROM SchlagzeilenEntity s " + 
           "WHERE lower(s.schlagzeile) LIKE lower(concat('%', :suchstring, '%'))" +
           "ORDER BY s.schlagzeile" )    
    List<SchlagzeilenEntity> sucheSchlagzeilen( @Param("suchstring") String suchstring,  
                                                Pageable pageable );

}

