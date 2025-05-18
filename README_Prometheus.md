# Prometheus-Konfiguration #

<br>

Die in diesem Repo enthaltene Spring-Boot-Anwendung ist so konfiguriert, dass sie über einen speziellen REST-Endpunkt 
(sog. [Actuator](https://docs.spring.io/spring-boot/docs/2.5.6/reference/html/actuator.html)
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
Mit `scrape_interval` wird festgelegt, dass die Metriken alle 11 Sekunden abgerufen werden.

<br>

----

## Verwendung Prometheus ##

<br>

Das Web-UI von Prometheus ist standardmäßig unter dem Port 9090 verfügbar, also bei lokaler Ausführung: 
http://localhost:8080/actuator/prometheus

<br>