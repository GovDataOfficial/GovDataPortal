
Installation
============

These instructions assume familiarity with all the required components and 
covers only those steps that are specific to the Open Data Platform.

Requirements
------------

The best point to start is a fresh Debian or Ubuntu GNU/Linux install. Most
other distribution should work as. A windows based system should be possible, 
too, but we have never seen it done.

- PostgreSQL 9.3 ([Postgres docs](http://postgres.de/install.html))
- Oracle Java 8 ([download Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html))
- Python 2.7 ([Python docs](http://www.python.org/getit/))
- Maven 3.1 ([Maven site](http://maven.apache.org/download.cgi))
- CKAN 2.3.x ([CKAN installation](http://docs.ckan.org/en/ckan-2.3.4/maintaining/installing/install-from-source.html)]), follow the ([Apache mod_wsgi instructions](http://docs.ckan.org/en/latest/maintaining/installing/deployment.html))
- Liferay 6.2.x ([Download site](http://www.liferay.com/de/downloads/liferay-portal/available-releases)) bundled with an application container, e.g. Tomcat for an easy start
- Elasticsearch 1.6.x follow the ([Elasticsearch installation guide](https://www.elastic.co/guide/en/elasticsearch/reference/1.6/setup-repositories.html))
- ActiveMQ ([ActiveMQ Getting started](http://activemq.apache.org/getting-started.html))
- ckanext-searchindexhook ([ckanext-searchindexhook](https://github.com/GovDataOfficial/ckanext-searchindexhook))

Prepare Database
---------------
If not already done, create database and user for Liferay:

    sudo su postgres
    psql -c "create user liferay with password 'yyy';"
    createdb -O liferay liferay -E utf-8 -T template0


Prepare CKAN
------------
Assuming you have an installed and reachable CKAN 2.3 installation.

- Create an admin user ([CKAN doc for that](http://docs.ckan.org/en/latest/maintaining/getting-started.html#create-admin-user))

- Note the admin API key, Liferay will be using this one to authenticate against CKAN (portal-ext.properties)

- Define the file *deutschland.json* with the licenses used by the German Open Data Portals. The file is available at [ogd-metadata](https://github.com/GovDataOfficial/ogd-metadata/blob/master/lizenzen/deutschland.json). This can be configured in the ckan configuration file *production.ini*, e.g.

  - directly to the remote url

    licenses_group_url = https://raw.githubusercontent.com/GovDataOfficial/ogd-metadata/master/lizenzen/deutschland.json

  - or to a local copy of the file

    licenses_group_url = file:///usr/lib/ogd-metadata/lizenzen/deutschland.json

- Create some categories (CKAN calls them "groups"). The ogd conform groups can be easily created by the CKAN command *groupadder* in the CKAN extension [ckanext-govdatade](https://github.com/GovDataOfficial/govdata/ckanext-govdatade).

- Create some datasets


Install ActiveMQ
---------------
Install ActiveMQ on the same host as the index services are running.


Install Index Services
----------------
- Install the Debian-Packages for the microservices index-application and index-queue-service, available in folder [deb](deb), e.g.:

    dpkg -i index-application_2.3.0+govd~ata_all.deb
    dpkg -i index-queue-service_2.3.0+govd~ata_all.deb

- Start Services

    sudo supervisorctl add index-queue-service index-application


Create Elasticsearch indexes
----------------
Create the necessary search indexes in Elasticsearch. Copy the Elasticsearch index configuration files from [index](index), e.g. to /etc/govdata/.

- create ckan index in Elasticsearch

    curl 'http://localhost:9200/{{ index_name_ckan }}' -f -X POST -H 'Content-Type:application/json;charset=utf-8'  --data '@/etc/govdata/de_ckan_govdata_index.json'
    
- create liferay index in Elasticsearch

    curl 'http://localhost:9200/{{ index_name_liferay }}' -f -X POST -H 'Content-Type:application/json;charset=utf-8'  --data '@/etc/govdata/de_liferay_govdata_index.json'

- create searchhistory index in Elasticsearch

    curl 'http://localhost:9200/{{ index_name_searchhistory }}' -f -X POST -H 'Content-Type:application/json;charset=utf-8'  --data '@/etc/govdata/de_searchhistory_govdata_index.json'

Replace the variables with the real index names, e.g. the default index names:

- {{ index_name_ckan }} = govdata-ckan-de

- {{ index_name_liferay }} = govdata-liferay-de

- {{ index_name_searchhistory }} = govdata-searchhistory-de


Install CKAN extension ckanext-searchindexhook
-----------------
Install the CKAN extension [ckanext-searchindexhook](https://github.com/GovDataOfficial/ckanext-searchindexhook) as described in the CKAN extension installation guide.


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


Build and deploy
----------------
Clone the main project:

    git clone https://github.com/GovDataOfficial/GovData.git govdata-portal
    cd govdata-portal

Build the portlets with Maven:

    mvn package

Put the WAR-Files in your deploy folder, e.g. /opt/app/portal-liferay/liferay-home/deploy. In the target folder you will find a debian package which put all necessary WAR-Files in the folder /opt/app/portal-liferay/liferay-home/deploy and copy the portal-ext.properties to /var/lib/tomcat7. Otherwise you can copy the WAR-files from the target folder of the several projects into the desired deploy folder, e.g.

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

Access control: You can use Liferay's [roles and permission](https://dev.liferay.com/discover/portal/-/knowledge_base/6-2/roles-and-permissions)
system for generic purposes like web content and page editing. The Open Data
Platform portlets in contrast _rely on CKAN's access control system_. This is
how it works: Users can register or be be registered in Liferay, the 
opendataregistry-client creates corresponding users in CKAN on the fly. So
if you want users to be able to add and edit datasets you have to configure
this using CKAN's user management by assigning a user to the desired organization with the role editor and
to the desired groups with the role member.


