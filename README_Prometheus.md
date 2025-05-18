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
            - targets: ["192.168.0.100:8080"]
```                            

Dieser Eintrag ist unter `scrape_configs` hinzuzufügen.

Bitte die IP-Adresse (hier: `192.168.0.100`) und ggf. auch die Port-Nummer (hier: `8080`) anpassen.
Mit `scrape_interval` wird festgelegt, dass die Metriken alle 11 Sekunden abgerufen werden;
der Default-Wert für `scrape_interval` ist `15s`
([Quelle](https://prometheus.io/docs/prometheus/latest/getting_started/#configuring-prometheus-to-monitor-itself)).

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
* Anzahl Log-Nachrichten mit Level "ERROR" in den letzten 5 Minuten:
  `increase(logback_events_total{level="error"}[5m])`

<br>
