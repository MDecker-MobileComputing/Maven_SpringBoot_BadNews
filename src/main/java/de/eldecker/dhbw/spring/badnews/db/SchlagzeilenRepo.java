package de.eldecker.dhbw.spring.badnews.db;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * In diesem Repository-Interface müssen wir nur abstrakte Methoden
 * definieren, die Implementierungen erzeugt <i>Spring Data JPA</i>
 * anhand der Methodennamen (Derived Query) oder einer über
 * Annotation definieren JPQL-Anweisung selbst hinzu.
 * <br><br>
 *
 * Da für die Anwendung eine Migration auf eine NoSQL-Datenbank
 * sehr unwahrscheinlich ist können wir als Super-Interface
 * {@ocde JpaRepository} verwenden.
 */
public interface SchlagzeilenRepo extends JpaRepository<SchlagzeilenEntity, Long> {

    /**
     * Derived Query: alle Inlandsnachrichten
     *
     * @return Schlagzeilen mit Ort im Inland, sortiert nach ID
     */
    List<SchlagzeilenEntity> findByInlandTrueOrderById();


    /**
     * Derived Query: alle Auslandsnachrichten
     *
     * @return Schlagzeilen mit Ort im Ausland, sortiert nach ID
     */
    List<SchlagzeilenEntity> findByInlandFalseOrderById();

    /*
        PageRequest pageRequest = PageRequest.of( 2, 100 ); // pages are zero-based
        Page<SchlagzeilenEntity> page = schlagzeilenRepo.findByInlandTrueOrderById(pageRequest); 
     */
    
    
    /**
     * Wie {@link #findByInlandTrueOrderById()}, aber mit Paginierung. 
     * 
     * @param pageable
     * @return
     */
    Page<SchlagzeilenEntity> findByInlandTrueOrderById( Pageable pageable );

    /**
     * Wie {@link #findByInlandFalseOrderById()}, aber mit Paginierung. 
     * 
     * @param pageable
     * @return
     */    
    Page<SchlagzeilenEntity> findByInlandFalseOrderById( Pageable pageable );
    
}

