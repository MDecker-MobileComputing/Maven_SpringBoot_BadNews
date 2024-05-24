package de.eldecker.dhbw.spring.badnews.db;

import org.springframework.data.jpa.repository.JpaRepository;


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
    
}

