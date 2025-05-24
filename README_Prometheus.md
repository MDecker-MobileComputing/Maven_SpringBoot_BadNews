# Prometheus-Konfiguration #

<br>

Die in diesem Repo enthaltene Spring-Boot-Anwendung ist so konfiguriert, dass sie über einen speziellen REST-Endpunkt
(sog. [Actuator](https://docs.spring.io/spring-boot/docs/2.5.6/reference/html/actuator.html))
Metriken im Format für das System-Monitoring-Tool [Prometheus](https://prometheus.io/) zur Verfügung stellt.

<br>

Zur Verwendung von Prometheus für eine Spring-Boot-Anwendung siehe auch: https://www.baeldung.com/spring-boot-prometheus

<br>

----

## Konfigurationen in Spring Boot ##

<br>

Abhängigkeiten in [pom.xml](pom.xml):
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <version>1.15.0</version>
</dependency>
```

<br>

Einträge in [application.properties](src/main/resources/application.properties):
```
management.endpoints.web.exposure.include=prometheus,health
management.endpoint.health.show-details=always
```

<br>

Die Metriken können dann als Textdatei (kein JSON-Format) vom folgenden Pfad abgerufen werden:
http://localhost:8080/actuator/prometheus

siehe [hier](https://gist.github.com/MDecker-MobileComputing/1689892114e48f31df71ae0e6a0aa8d8)
für ein Beispiel.

<br>

----

## Konfiguration von Prometheus ##

<br>

Konfiguration in Datei `prometheus.yml`, damit Prometheus regelmäßig die über den Endpunkt bereitgestellten
Metriken einsammelt:

```
- job_name: "spring-actuator"
    metrics_path: "/actuator/prometheus"
    scrape_interval: 11s
    static_configs:
        - targets: ["localhost:8080"]
```

Dieser Eintrag ist unter `scrape_configs` hinzuzufügen; bitte Host (hier: `localhost`) und ggf. auch die Port-Nummer
(hier: `8080`) im von `targets` referenzierten Array anpassen.

Mit `scrape_interval` wird festgelegt, dass die Metriken alle 11 Sekunden abgerufen werden;
der Default-Wert für `scrape_interval` ist `15s`
([Quelle](https://prometheus.io/docs/prometheus/latest/getting_started/#configuring-prometheus-to-monitor-itself)).

<br>

[Beispiel für komplette Konfigurationsdatei](https://gist.github.com/MDecker-MobileComputing/7930b44a528e171521c0f54c7940efc3)

<br>

Per Default speichert Prometheus die Metriken 14 Tage lang
([Quelle](https://prometheus.io/docs/prometheus/latest/storage/#operational-aspects)).

<br>

----

## Verwendung Prometheus ##

<br>

Das Web-UI von Prometheus ist standardmäßig unter dem Port 9090 verfügbar, also bei lokaler Ausführung:
http://localhost:9090

<br>

In der Unterseite "Query" ( http://localhost:9090/query ) können im Tab "Graph" z.B. folgende Metriken
abgerufen werden:

* CPU-Auslastung: `system_cpu_usage`
* Anzahl Log-Nachrichten: `logback_events_total`

<br>

Mit der Prometheus-eigenen Abfragesprache [PromQL](https://prometheus.io/docs/prometheus/latest/querying/basics/)
können auch komplexere Abfragen gestellt werden, z.B.:

* Durchschnittlichen Antwortzeit in den letzten 5 Minuten:
  `rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])`
* Anzahl der HTTP-Requests in den letzten 5 Minuten:
  `increase(http_server_requests_seconds_count[5m])`
* Anzahl Log-Nachrichten mit Log-Level "error" in den letzten 5 Minuten:
  `increase(logback_events_total{level="error"}[5m])`

<br>

----

## Eigene Metrik ##

<br>

In der Klasse `EigenePrometheusMetriken` wird eine eigene Metrik definiert, die die Gesamtanzahl der Suchvorgänge
beschreibt. Technischer Name dieser Metrik: `badnews_suchvorgaenge_total`

<br>

Beispiel für Zeilen für diese Metriken in der von Mikrometer bereitgestellten Textdatei für insgesamt zwei Suchvorgänge:

```
 # HELP badnews_suchvorgaenge_total Anzahl der Suchvorgänge
 # TYPE badnews_suchvorgaenge_total counter
 badnews_suchvorgaenge_total{funktion="suche",umgebung="development"} 2.0
 ```

<br>