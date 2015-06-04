Dokumentation
=============

Die Open Data Platform integriert verschiedene Komponenten zu einer vollständigen Portallösung für offene Daten. Insbesondere bindet sie die Metadaten-Katalogsoftware [CKAN](http://ckan.org) in eine JavaEE-Server-Umgebung ein.


Komponenten
-----------

- Die Portalsoftware Liferay wird in einem Java-Application-Container wie Apache Tomcat ausgeführt. Sie implementiert den Portlet-Standard JSR 268. Die Anwendungen der Open Data Platform sind als Portlets ausgeführt, die in diesem Portal laufen.

- CKAN ist ein für Offene Daten weit verbreiteter Metadaten-Katalog, mit vollständiger REST-API. Zur Anbindung an Java-Anwendungenn wurde eine Java-Client-Bibliothek für die CKAN-API entwickelt (opendataregistry-client). Derzeit (August 2013) wird nur CKAN 1.x unterstützt, die Anpassung an CKAN 2.x sollte jedoch unkompliziert sein. Grundsätzlich können auch andere Datenkataloge angeschlossen werden.

- Typischerweise wird beiden ein Webserver wie der Apache httpd vorgeschaltet, um Http-Anfragen den Anwendungen oder statischen Dateien zuzuordnen

- Als Persistenz-Komponente dient ein Datenbank-Server. CKAN arbeitet nur mit PostgreSQL zusammen, Liferay unterstützt alle vom Hibernate-Framework unterstützen Datenbanken also auch PostgreSQL.

- Noch wichtiger als die manuelle Dateneingabe ist für die Open Data Plattform das automatische Importieren der Metadaten aus entfernten Datenkatalog, auch Harvesting genannt. Die als CKAN-Erweiterung umgesetzten Harvester erledigen diese Aufgabe https://github.com/fraunhoferfokus/ckanext-govdatade

Architektur
-----------

Grundsätzlich sind viele der Komponenten flexibel austauschbar. Ein typischer Aufbau sieht wie folgt aus:

Ein Apache httpd empfängt alle Anfragen per HTTPS. Statische Dateien wie Dumps und Logs werden sofort ausgeliefert. Normale Portalanfragen werden per Reverse Proxy an einen oder bei Hochverfügbarkeit mehrere Tomcat-Prozesse weitergeleitet. CKAN wird per WSGI ebenfall in einem Apache httpd ausgeführt und bedient die HTTP-REST-Anfragen der Liferay-Anwendungen und die API-Anfragen von außenn. Sowohl Liferay als auch CKAN greif per PostgrSQL-Client auf den Datenbank-Prozess zu.

![Setup](https://gitlab.fokus.fraunhofer.de/opendataplatform/govdata-ui/raw/master/doc/simple-setup.svg)

Diese Architektur kann sowohl auf einer einzelnen Server-Maschine als auch in einer Microservice-Umgebung umgesetzt werden, da alle Prozesse per Netzwerk miteinander kommunizieren.

Die einzelnen Java-Komponenten der Open Data Platform sind als Portlets umgesetzt, d.h. lose gekoppelt und können grundsätzlich auch einzeln eingesetzt werden. Zentrale Bibliothek ist der opendataregistry-client, der den Oberflächen-Portlets eine einfach Java-API bietet, sodass nicht jedes Portlet selbst direkt mit der REST-Schnittstelle von CKAN interagieren muss.

![Portlets](https://gitlab.fokus.fraunhofer.de/opendataplatform/govdata-ui/raw/master/doc/portlet-architecture.svg)

[opendataregistry-client](../master/opendataregistry-client): Zentrale Bibliothek, Java-API für CKAN.

[categories-grid-portlet](../master/categories-grid-portlet): Zeigt die Kategorien mit Pikogrammen und Anzahl der Datensätze an (Startseite)

[boxes-portlet](../master/boxes-portlet): Zeigt neuste Datensätze, Dokumente und Apps (Startseite)

[cache-scheduler](../master/cache-scheduler): Hält die zwischengespeicherten Inhalte von categories-grid-portlet und boxes-portlet aktuell.

[search-gui-portlet](../master/search-gui-portlet): Zeigt die Suchmaske an und leitet die Anfrage per IPC an das dataset-portlet weiter.

[dataset-portlet](../../dataset-portlet): Zeigt die Suchergebnis-Liste mit Filtern und die Datensatzdetailseite an, die es per opendataregistry-client von CKAN holt.

[manage-datasets-portlet](../../manage-datasets-portlet): Erlaubt das erstellen und pflegen von Metadaten per Formular und opendataregistry-client.

[entities](../master/odp-entities): Ermöglichst das persistieren von Kommentaren zu Datensätzen durch JPA.

[govdata-theme](../master/govdata-theme): Oberflächengestaltung

[rss-servlet](../master/rss-servlet): Betten den CKAN-RSS-Feed ins Portal ein.

[screennamevalidator-hook](../master/screennamevalidator-hook): Stellt sicher, dass in Liferay erzeugte Benutzernamen auch in CKAN valide sind.

[language-hook](../master/language-hook): Dient der Internationalisierung von Texten.

[errorpages-hook](../master/errorpages-hook): Zeigt angepasste Fehlerseiten an.


Links:
------

- [Installing ODP](./INSTALL.md)
- [Contributing to ODP](./CONTRIBUTING.md)
- [Open data metadata structure for Germany](https://github.com/fraunhoferfokus/ogd-metadata)
- Relevant CKAN plugins
  - [harvesting extension used at govdata.de](https://github.com/fraunhoferfokus/ckanext-govdatade)
  - [harvesting extension for CSW/ISO19115 geo metadata servers](https://github.com/fraunhoferfokus/ckanext-spatial/tree/ogpd)
  - [harvesting extension for specific formats specified by INSPIRE](https://github.com/fraunhoferfokus/ckanext-inspire)
  - [Geo related plugins for CKAN](https://github.com/fraunhoferfokus/ckanext-spatial)
- [Fraunhofer FOKUS Open Data Blog](http://open-data.fokus.fraunhofer.de), esp. [on the architecture](http://open-data.fokus.fraunhofer.de/?p=1154&lang=en), 
[on the metadata structure](http://open-data.fokus.fraunhofer.de/?p=643&lang=en), [on harvesting](http://open-data.fokus.fraunhofer.de/?p=2418&lang=en)
