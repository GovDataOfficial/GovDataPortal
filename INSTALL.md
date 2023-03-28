
Installation
============

These instructions assume familiarity with all the required components and 
covers only those steps that are specific to the Open Data Platform.

Requirements
------------

The best point to start is a fresh Debian or Ubuntu GNU/Linux install. Most
other distribution should work as. A windows based system should be possible, 
too, but we have never seen it done.

- PostgreSQL 10 ([Postgres docs](https://www.postgresql.org/docs/))
- Java JDK 8 / 11 ([OpenJDK JDK download](https://adoptopenjdk.net/) or [Oracle Java JDK download](https://www.oracle.com/java/technologies/downloads/archive/))
- Python 2.7 / 3 ([Python docs](https://www.python.org/getit/))
- Maven 3.6.x ([Maven site](http://maven.apache.org/download.cgi))
- CKAN 2.x ([CKAN installation](https://docs.ckan.org/en/latest/maintaining/installing/install-from-source.html)), follow the ([Apache mod_wsgi instructions](https://docs.ckan.org/en/latest/maintaining/installing/deployment.html))
- Liferay 7.3.x ([GitHub Releases](https://github.com/liferay/liferay-portal/releases/)) bundled with an application container, e.g. Tomcat for an easy start
- Elasticsearch 7.17.x follow the ([Elasticsearch installation guide](https://www.elastic.co/guide/en/elasticsearch/reference/7.17/index.html))
- ActiveMQ ([ActiveMQ Getting started](http://activemq.apache.org/getting-started.html))
- ckanext-searchindexhook ([on GitHub](https://github.com/GovDataOfficial/ckanext-searchindexhook) or [on Open CoDE](https://gitlab.opencode.de/fitko/govdata/ckanext-searchindexhook))


Optional Requirements:
- Flyway 7.8.x ([Flyway Download and installation](https://flywaydb.org/documentation/usage/commandline/#download-and-installation), [Flyway command-line tool download](https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/7.8.2/))
- TripleStore Apache Jena Fuseki ([Apache Jena Fuseki documentation](https://jena.apache.org/documentation/fuseki2/), [Download Apache Jena Fuseki](https://jena.apache.org/download/))
- SHACL validator ([ITB SHACL validator documentation](https://www.itb.ec.europa.eu/docs/guides/latest/index.html), [ DCAT-AP.de SHACL validator download](https://www.itb.ec.europa.eu/shacl-jar/dcat-ap.de/validator.zip), [Online DCAT-AP.de SHACL validator](https://www.itb.ec.europa.eu/shacl/dcat-ap.de/upload))

Prepare Database
---------------
If not already done, create database and user for Liferay:

    sudo su postgres
    psql -c "create user liferay with password 'liferay';"
    createdb -O liferay liferay -E utf-8 -T template0


Prepare CKAN
------------
Assuming you have an installed and reachable CKAN 2.x installation.

- Create an admin user ([CKAN doc for that](http://docs.ckan.org/en/latest/maintaining/getting-started.html#create-admin-user))

- Note the admin API key, Liferay will be using this one to authenticate against CKAN (portal-ext.properties)

- Define the file with the licenses used by the German Open Data Portals. The file is available at **ckanext-dcatde** ([on GitHub](https://github.com/GovDataOfficial/ckanext-dcatde/blob/master/examples/dcat_licenses.json) or [on Open CoDE](https://gitlab.opencode.de/fitko/govdata/ckanext-dcatde/-/blob/master/examples/dcat_licenses.json)). This can be configured in the ckan configuration file *production.ini*, e.g.

  - directly to the remote url

        licenses_group_url = https://raw.githubusercontent.com/GovDataOfficial/ckanext-dcatde/master/examples/dcat_licenses.json
        # or
        licenses_group_url = https://gitlab.opencode.de/fitko/govdata/ckanext-dcatde/-/raw/master/examples/dcat_licenses.json

  - or to a local copy of the file

        licenses_group_url = file:///usr/lib/dcat-metadata/dcat_licenses.json

- Create some categories (CKAN calls them "groups"). The DCAT-AP.de conform groups can be easily created by the CKAN command *dcatde_themeadder* in the CKAN extension **ckanext-dcatde** ([on GitHub](https://github.com/GovDataOfficial/ckanext-dcatde#creating-dcat-ap-categories-as-groups) or [on Open CoDE](https://gitlab.opencode.de/fitko/govdata/ckanext-dcatde#creating-dcat-ap-categories-as-groups)).

- Create some datasets


Install ActiveMQ
---------------
Install ActiveMQ on the same host where the index services are running.


Install Flyway
---------------
Install Flyway on the same host where the DB service is running.


Install Index Services (Queue + Elasticsearch) and DB service (Showcases)
----------------
- Install the Debian-Packages for the microservices index-application2-service, index-queue-service and govdata-db-service, available in folder [deb](deb), e.g.:

        sudo dpkg -i index-application2-service_*_all.deb
        sudo dpkg -i index-queue-service_*_all.deb
        sudo dpkg -i govdata-db-service_*_all.deb

- Initial tasks before first start

        sudo flyway -configFiles=/opt/app/govdata-db-service/config/flyway.properties clean

- Start Services

        sudo supervisorctl add index-queue-service index-application2-service govdata-db-service
        sudo supervisorctl start index-queue-service index-application2-service govdata-db-service


Create Elasticsearch indexes
----------------
Create the necessary search indexes in Elasticsearch. Copy the Elasticsearch index configuration files from [index](index), e.g. to /etc/govdata/.

- create ckan index in Elasticsearch

        curl 'http://localhost:9200/{{ index_name_ckan }}' -f -X PUT -H 'Content-Type:application/json;charset=utf-8'  --user "{{ elastic_user }}:{{ elastic_user_pw }} --data '@/etc/govdata/de_ckan_govdata_index.json'
        curl 'http://localhost:9200/{{ index_name_ckan }}/_mapping' -f -X POST -H 'Content-Type:application/json;charset=utf-8'  --user "{{ elastic_user }}:{{ elastic_user_pw }} --data '@/etc/govdata/de_ckan_govdata_index_mapping.json'

- create liferay index in Elasticsearch

        curl 'http://localhost:9200/{{ index_name_liferay }}' -f -X PUT -H 'Content-Type:application/json;charset=utf-8'  --user "{{ elastic_user }}:{{ elastic_user_pw }} --data '@/etc/govdata/de_liferay_govdata_index.json'
        curl 'http://localhost:9200/{{ index_name_liferay }}/_mapping' -f -X POST -H 'Content-Type:application/json;charset=utf-8'  --user "{{ elastic_user }}:{{ elastic_user_pw }} --data '@/etc/govdata/de_liferay_govdata_index_mapping.json'

- create searchhistory index in Elasticsearch

        curl 'http://localhost:9200/{{ index_name_searchhistory }}' -f -X PUT -H 'Content-Type:application/json;charset=utf-8'  --user "{{ elastic_user }}:{{ elastic_user_pw }} --data '@/etc/govdata/de_searchhistory_govdata_index.json'
        curl 'http://localhost:9200/{{ index_name_searchhistory }}/_mapping' -f -X POST -H 'Content-Type:application/json;charset=utf-8'  --user "{{ elastic_user }}:{{ elastic_user_pw }} --data '@/etc/govdata/de_searchhistory_govdata_index_mapping.json'

- create showcases index in Elasticsearch

        curl 'http://localhost:9200/{{ index_name_showcases }}' -f -X PUT -H 'Content-Type:application/json;charset=utf-8'  --user "{{ elastic_user }}:{{ elastic_user_pw }} --data '@/etc/govdata/de_showcases_govdata_index.json'
        curl 'http://localhost:9200/{{ index_name_showcases }}/_mapping' -f -X POST -H 'Content-Type:application/json;charset=utf-8'  --user "{{ elastic_user }}:{{ elastic_user_pw }} --data '@/etc/govdata/de_showcases_govdata_index_mapping.json'

Replace the variables with the real index names, e.g. the default index names:

- {{ index_name_ckan }} = govdata-ckan-de

- {{ index_name_liferay }} = govdata-liferay-de

- {{ index_name_searchhistory }} = govdata-searchhistory-de

- {{ index_name_showcases }} = govdata-showcases-de

- {{ elastic_user }} = elastic

- {{ elastic_user_pw }} = pass


Install CKAN extension ckanext-searchindexhook
-----------------
Install the CKAN extension **ckanext-searchindexhook** ([on GitHub](https://github.com/GovDataOfficial/ckanext-searchindexhook) or [on Open CoDE](https://gitlab.opencode.de/fitko/govdata/ckanext-searchindexhook)) as described in the CKAN extension installation guide.


Configure the portal:
-----------------
Stop your application server and edit `portal-ext.properties`. We recommend using the portal-ext.properties file used for http://www.govdata.de in packaging/src/main/resources/ and update the relevant parameters.

The relevant parameters were:

- cKANurlFriendly

- authenticationKey

- jdbc.default.url

- jdbc.default.username

- jdbc.default.password

- elasticsearch.cluster

The following parameters could be relevant if some components are not installed on the same host:

- cKANurl

- cluster.link.autodetect.address

- mail.session.mail.smtp.host

- ckanext.govdata.validators.redis.host

- service.url

- elasticsearch.nodes

For different index names in elasticsearch the following parameters are also relevant:

- elasticsearch.liferay.index.name

- elasticsearch.search.index.searchhistory

- elasticsearch.search.index.names

- elasticsearch.search.index.showcases

Relevant parameters when using a TripleStore Apache Jena Fuseki:

- fusekiUrl

- fusekiSparqlEndpoint

- fusekiDatastoreName

Relevant parameters when using a SHACL validator:

- shaclValidatorEndpoint

- shaclValidatorProfileType

- fusekiShaclSparqlEndpoint

- fusekiShaclDatastoreName


Build and deploy
----------------
Clone the main project (from GitHub):

    git clone https://github.com/GovDataOfficial/GovDataPortal.git govdata-portal
    cd govdata-portal

or (from Open CoDE):

    git clone https://gitlab.opencode.de/fitko/govdata/GovDataPortal.git govdata-portal
    cd govdata-portal

Build the portlets with Maven:

    mvn package

Put the WAR-Files in your deploy folder, e.g. /opt/app/portal-liferay/liferay-home/deploy. In the target folder you will find a debian package which put all necessary WAR-Files in the folder /opt/app/portal-liferay/liferay-home/deploy and copy the portal-ext.properties to /var/lib/tomcat9. Otherwise you can copy the WAR-files from the target folder of the several projects into the desired deploy folder, e.g.

    cp $(find . -name "*.war") /opt/liferay/deploy


Start your application server:
-----------------
You are ready to create your data portal!

As a first step assign the Theme "Seitenbau GovData Theme" to all Public Pages.

Next, create a landing page and place the 
portlets *categories-grid-portlet* and *boxes-portlet* from the group *odp-platform* on the page.

Next create a page for the map search, e.g. "Kartensuche" with the friendly url "kartensuche" and place the portlet *GovData Kartensuche* on the page. For all portlets the parameter "Show borders" within the *Look and Feel* should be set to "No".

After this create a page for the extended search, e.g. "Erweiterte Suche" with the friendly url "erweitertesuche" and place the portlet *GovData erweiterte Suche* on the page.

Next create a Liferay page *suchen* and place the portlets *GovData Suchfeld*, *GovData Suchergebnisse* and
*GovData Metadata Detailseite* from the group *GovData*. On this page it is possible to search, view the
search result and view the details of the datasets. At least put the portlet *GovData Suchergebnisse als Atomfeed* at the end of the page. For all portlets the parameter "Show borders" within the *Look and Feel* should be set to "No".

Typically you would instantiate *GovData Metadaten bearbeiten* on a hidden page, so that it is seen only by
user who are allowed to manipulate datasets. The user with the right to manipulate datasets should have
assigned to the role "Datenbereitsteller" in Liferay. The role have to be created if not existent. For the added portlet the parameter "Show borders" within the *Look and Feel* should be set to "No".

Access control: You can use Liferay's [roles and permission](https://help.liferay.com/hc/en-us/articles/360017895212-Roles-and-Permissions)
system for generic purposes like web content and page editing. The Open Data
Platform portlets in contrast _rely on CKAN's access control system_. This is
how it works: Users can register or be be registered in Liferay, the 
opendataregistry-client creates corresponding users in CKAN on the fly. So
if you want users to be able to add and edit datasets you have to configure
this using CKAN's user management by assigning a user to the desired organization with the role editor and
to the desired groups with the role member.


