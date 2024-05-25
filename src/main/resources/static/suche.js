"use strict";


/**
 * Event-Handler-Funktion für Button zum Auslösen einer Suche.
 *
 * @returns {Boolean} `false`, um Laden einer anderen Seite zu verhindern.
 */
function onSucheButton() {

    const ergebnisDiv = document.getElementById( "ergebnis" );
    if ( !ergebnisDiv ) {
            
            alert( "Interner Fehler: DIV für Suchergebnis nicht gefunden." );
            return;
    }    
    ergebnisDiv.innerHTML = "";

    const textfeld = document.getElementById( "suchbegriff" );
    if ( !textfeld ) {

        alert( "Interner Fehler: Textfeld mit Suchbegriff nicht gefunden." );
        return false;
    }

    let suchbegriff = textfeld.value;
    if ( suchbegriff === undefined || suchbegriff === null ) {

        alert( "Interner Fehler: Suchbegriff konnte nicht aus Textfeld ausgelesen werden." );
        return false;
    }

    suchbegriff = suchbegriff.trim();

    if ( suchbegriff.length < 3 ) {

        alert( "Bitte geben Sie mindestens 3 Zeichen als Suchbegriff ein.");
        return false;
    }

    const url = "/api/v1/suche?query=" + encodeURIComponent( suchbegriff );

    fetch( url, {
        method: "GET",
        headers: { "Content-Type": "text/plain" },
    })
    .then( response => {

        if (!response.ok) {

            const statusText = `${response.statusText} (${response.status})`;
            throw new Error( `REST-Endpunkt hat Fehlercode zurückgeliefert: ${statusText}` );

        } else {

            return response.text() ;
        }
    })
    .then( antwortString => {

        suchergebnisDarstellen( antwortString );

    })
    .catch( (fehler) => {

        const fehlerText = "Fehler bei Such-Request: " + fehler;
        console.error( fehlerText );
        alert( fehlerText );
    });    

    return false;
}


/**
 * Suchergebnis als Liste von Schlagzeilen auf Seite darstellen.
 * 
 * @param {string} antwortString 
 */
function suchergebnisDarstellen( antwortString ) {

    const ergebnisDiv = document.getElementById( "ergebnis" );
    if ( !ergebnisDiv ) {
            
            alert( "Interner Fehler: DIV für Suchergebnis nicht gefunden." );
            return;
    }

    const ergebnisArray = JSON.parse( antwortString );

    console.log( `Anzahl Schlagzeilen erhalten: ${ergebnisArray.length}` );

    ergebnisArray.forEach(item => {

        console.log( `Schlagzeile mit ID=${item.id} gefunden: ${item.schlagzeile}` );

        const p = document.createElement( "p" );
        p.textContent = item.schlagzeile;
        ergebnisDiv.appendChild( p );
    });
}


/**
 * Event-Handler für Reset-Button
 */
function onResetButton() {

    const textfeld = document.getElementById( "suchbegriff" );
    if ( !textfeld ) {

        alert( "Interner Fehler: Textfeld mit Suchbegriff nicht gefunden." );
        return false;
    }

    textfeld.value = "";

    return false;
}