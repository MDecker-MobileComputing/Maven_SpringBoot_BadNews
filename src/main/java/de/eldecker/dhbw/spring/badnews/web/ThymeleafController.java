package de.eldecker.dhbw.spring.badnews.web;

import static org.springframework.data.domain.Sort.Direction.ASC;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenEntity;
import de.eldecker.dhbw.spring.badnews.db.SchlagzeilenRepo;


/**
 * Controller-Klasse für Thymeleaf-Templates. Jetzt Mapping-Methode
 * gibt den String mit dem Namen der Template-Datei (ohne Datei-Endung)
 * zurück, die angezeigt werden soll.
 */
@Controller
@RequestMapping( "/app/" )
public class ThymeleafController {

    /** Repo-Bean für Zugriff auf Tabelle mit Schlagzeilen. */
    private SchlagzeilenRepo _repo;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public ThymeleafController( SchlagzeilenRepo repo ) {

        _repo = repo;
    }


    /**
     * Eine Seite mit Schlagzeilen anzeigen.
     *
     * @param model Objekt, in dem die Werte für die Platzhalter in der Template-Datei
     *              definiert werden.
     *
     * @param seite Optionaler URL-Parameter für die Seitennummer, 1-basiert;
     *              Default-Wert: 1
     * 
     * @param anzahl Optionaler URL-Parameter für Anzahl Schlagzeilen auf einer Seite;
     *               Default-Wert: 10 
     *
     * @return Name Template-Datei "schlagzeilen"
     */
    @GetMapping( "/schlagzeilen/" )
    public String schlagzeilenAnzeigen( Model model,
                                        @RequestParam( value = "seite" , required = false, defaultValue = "1"  ) int seite ,
                                        @RequestParam( value = "anzahl", required = false, defaultValue = "10" ) int anzahl ) {                                                                                                              

        final Sort sortierung = Sort.by( ASC, "id" ); 
        final PageRequest seitenRequest = PageRequest.of( seite, 10, sortierung ); // seitenwert ist 0-basiert
        final Page<SchlagzeilenEntity> ergebnisSeite = _repo.findAll( seitenRequest );

        final List<SchlagzeilenEntity> schlagzeilenListe = ergebnisSeite.getContent();
        
        model.addAttribute( "schlagzeilenliste", schlagzeilenListe );
        
        return "schlagzeilen";
    }

}
