Dokumentation
=============

Die Open Data Platform integriert verschiedene Komponenten zu einer vollständigen Portallösung für offene Daten. Insbesondere bindet sie die Metadaten-Katalogsoftware [CKAN](http://ckan.org) in eine JavaEE-Server-Umgebung ein.


Komponenten
-----------

- Die Portalsoftware Liferay wird in einem Java-Application-Container wie Apache Tomcat ausgeführt. Sie implementiert den Portlet-Standard JSR 268. Die Anwendungen der Open Data Platform sind als Portlets implementiert, die in diesem Portal laufen.

- CKAN ist ein für Offene Daten weit verbreiteter Metadaten-Katalog, mit vollständiger REST-API. Zur Anbindung an Java-Anwendungenn wurde eine Java-Client-Bibliothek für die CKAN-API entwickelt (opendataregistry-client). Unterstützt wird CKAN 2.x. Grundsätzlich können auch andere Datenkataloge angeschlossen werden.

- Typischerweise wird beiden ein Webserver wie der Apache httpd vorgeschaltet, um HTTP-Anfragen den Anwendungen oder statischen Dateien zuzuordnen

- Elasticsearch für die Speicherung des Suchindex. Der Suchindex wird mit dem SOLR-Suchindex von CKAN synchron gehalten und von den Liferay-Portlets genutzt, um die Suchanfragen performant zu bearbeiten und die Suchergebnisliste mit den verschiedenen Filtern darzustellen. Derzeit wird Elasticsearch in Version 1.6.x unterstützt, da diese für die Long-Time-Support (LTS) Version von Ubuntu Server verfügbar ist. Für die Synchronisation der Suchindexe ist die CKAN-Erweiterung [ckanext-searchindexhook](https://github.com/GovDataOfficial/ckanext-searchindexhook) zuständig.

- Als Persistenz-Komponente dient ein Datenbank-Server. CKAN arbeitet nur mit PostgreSQL zusammen, Liferay unterstützt alle vom Hibernate-Framework unterstützen Datenbanken also auch PostgreSQL.

- Noch wichtiger als die manuelle Dateneingabe ist für die Open Data Plattform das automatische Importieren der Metadaten aus entfernten Datenkatalog, auch Harvesting genannt. Für den automatisierten Import von Daten wurden individuelle Harvester auf Basis der CKAN-Erweiterung ckanext-harvest implementiert. Diese sind in der CKAN-Erweiterung [ckanext-govdatade](https://github.com/GovDataOfficial/ckanext-govdatade) enthalten.

Architektur
-----------

Grundsätzlich sind viele der Komponenten flexibel austauschbar. Ein typischer Aufbau sieht wie folgt aus:

Ein Apache httpd empfängt alle Anfragen per HTTPS. Statische Dateien wie Dumps und Logs werden sofort ausgeliefert. Normale Portalanfragen werden per Reverse Proxy an einen oder bei Hochverfügbarkeit mehrere Tomcat-Prozesse weitergeleitet. CKAN wird per WSGI ebenfalls in einem Apache httpd ausgeführt und bedient die HTTP-REST-Anfragen der Liferay-Anwendungen und die API-Anfragen von außen. Für http://www.govdata.de läuft CKAN statt mit dem Apache Modul mod_wsgi mit dem WSGI HTTP Server Gunicorn (http://gunicorn.org/), was das Python Virtual Environment besser von dem des Betriebsystems trennt. Sowohl Liferay als auch CKAN greifen per PostgreSQL-Client auf den Datenbank-Prozess zu.

Diese Architektur kann sowohl auf einer einzelnen Server-Maschine als auch in einer Microservice-Umgebung umgesetzt werden, da alle Prozesse per Netzwerk miteinander kommunizieren.

Die einzelnen Java-Komponenten der Open Data Platform sind als Portlets umgesetzt, d.h. lose gekoppelt und können grundsätzlich auch einzeln eingesetzt werden. Zentrale Bibliothek ist der opendataregistry-client, der den Oberflächen-Portlets eine einfach Java-API bietet, sodass nicht jedes Portlet selbst direkt mit der REST-Schnittstelle von CKAN interagieren muss.

[opendataregistry-client](opendataregistry-client): Zentrale Bibliothek, Java-API für CKAN.

[categories-grid-portlet](categories-grid-portlet): Zeigt die Kategorien mit Icons und Anzahl der Datensätze an (Startseite)

[boxes-portlet](boxes-portlet): Zeigt neuste Datensätze, Dokumente und Apps (Startseite)

[gd-search](gd-search): Zeigt die Suchmaske und die Suchergebnis-Liste mit Filtern an. Die Suchanfragen erfolgen an Elasticsearch, die die nötigen Informationen für die Darstellung der Suchergebnissliste liefert. Für die Anzeige der Datensatzdetailseite werden die Informationen per opendataregistry-client von CKAN holt. Das Portlet enthält auch einen Hook, mit dem die Liferay-Inhalte, z.B. Blog-Beiträge in den Elasticsearch-Index geschrieben werden.

[gd-edit-portlet](gd-edit-portlet): Erlaubt das erstellen und pflegen von Metadaten per Formular und opendataregistry-client.

[gd-usermanage-portlet](gd-usermanage-portlet): Enthält und aktiviert die folgenden Funktionalitäten:
- Benutzer können ihr Konto mittels Double-Opt-Out selbst löschen.
- Bei der Eingabe von Formularen wird statt einem Captcha eine alternative Implementierung genutzt.

[entities](entities): Ermöglichst das persistieren von Kommentaren zu Datensätzen durch JPA.

[govdatastyle-theme](govdatastyle-theme): Das Theme für das Design von http://www.govdata.de.

[screenname-hook](screenname-hook): Stellt sicher, dass in Liferay erzeugte Benutzernamen auch in CKAN valide sind.

[language-hook](language-hook): Dient der Internationalisierung von Texten.

[Layout20-80-layouttpl](Layout20-80-layouttpl): Wird als Layout für die Seiten unter dem Bereich Informationen genutzt.


Links:
------

- [Installing ODP](./INSTALL.md)
- [Contributing to ODP](./CONTRIBUTING.md)
- [Open data metadata structure for Germany](http://www.dcat-ap.de/def/)
- Relevant CKAN plugins
  - [searchindex extension used at govdata.de](https://github.com/GovDataOfficial/ckanext-searchindexhook)
  - [german specific dcat extension used at govdata.de](https://github.com/GovDataOfficial/ckanext-dcatde)
  - [harvesting extension used at govdata.de](https://github.com/GovDataOfficial/ckanext-govdatade)

