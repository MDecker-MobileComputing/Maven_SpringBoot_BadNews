package de.eldecker.dhbw.spring.badnews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Klasse mit Einstiegsmethode, kann im Debugger ausgeführt werden.
 */
@SpringBootApplication
public class BadNewsApplication {

	public static void main( String[] args ) {
	    
		SpringApplication.run( BadNewsApplication.class, args );
	}

}
