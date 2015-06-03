
Installation
============

These instructions assume familiarity with all the required components and 
covers only those steps that are specific to the Open Data Platform.

Requirements
------------

The best point to start is a fresh Debian or Ubuntu GNU/Linux install. Most
other distribution should work as. A windows based system should be possible, 
too, but we have never seen it done.

- PostgreSQL 9 ([Postgres docs](http://postgres.de/install.html))
- OpenJDK 6 or 7 ([download Java](http://www.java.com/de/download/))
- Python 2.7 ([Python docs](http://www.python.org/getit/))
- Maven 3.0 ([Maven site](http://maven.apache.org/download.cgi))
- CKAN 1.x ([CKAN installation](http://docs.ckan.org/en/ckan-1.8.1/installing.html)]), follow the [Apache mod_wsgi instructions](http://docs.ckan.org/en/ckan-1.8.1/deployment.html#install-apache-and-modwsgi))
- Liferay 6.1 ([Download site](http://www.liferay.com/de/downloads/liferay-portal/available-releases)) bundled with an application container, e.g. Tomcat


Prepare Database
----------------

Create database and user for Liferay:

    sudo su postgres
    psql -c "create user liferay with password 'yyy';"
    createdb -O liferay liferay

Prepare CKAN
------------
- Create an admin user ([CKAN doc for that](http://docs.ckan.org/en/ckan-1.7.3/post-installation.html#create-an-admin-user))
- Note the admin API key, Liferay will be using this one to authenticate against CKAN
- Create some categories (CKAN calls them "groups"), e.g. through the web interface
- Create some datasets

Build and deploy
----------------
Clone the main project and tell git to clone the submodules, too:

    git clone <path-to-govdata-ui>
    cd govdata-ui
    git submodule init
    git submodule update

Build the portlets with Maven:

    mvn -f entities/pom.xml liferay:build-service
    mvn package

Put the .WARs in your deploy folder, e.g.:

    cp $(find . -name "*.war") /opt/liferay/deploy

Configure the portal:
-----------------
Stop your application server and edit `portal-ext.properties`, add at least the following lines:

    # The CKAN url as seen from your application server.
    cKANurl=http://localhost:5000/
    # The CKAN url as seen from your users.
    cKANurlFriendly=http://www.thenewdataportal.org/ckan/
    # Liferays authentication key for CKAN.
    authenticationKey=c71418e2-bd06-488c-8b72-93ce82760067
    
    #DB config
    jdbc.default.driverClassName=org.postgresql.Driver
    jdbc.default.url=jdbc:postgresql://localhost:5432/liferay
    jdbc.default.username=liferay
    jdbc.default.password=xxx

Start your application server.

You are ready to create your data portal! As a first step create a Liferay 
page and instantiate the portlets *Search GUI* and *Dataset*, as these provide 
the view on your datasets. Next, create a landing page and instantiate the 
portlets *Categories Grid* and *Boxes* from the group *Open Data Platform*. 
Choose the custom theme as default theme for public pages. Typically you would
instantiate *Manage Datasets* on a hidden page, so that it is seen only by
user who are allowed to manipulate datasets.

Access control: You can use Liferay's [roles and permission](http://www.liferay.com/documentation/liferay-portal/6.1/user-guide/-/ai/lp-6-1-ugen15-roles-and-permissions-0)
system for generic purposes like web content and page editing. The Open Data
Platform portlets in contrast _rely on CKAN's access control system_. This is
how it works: Users can register or be be registered in Liferay, the 
opendataregistry-client creates corresponding users in CKAN on the fly. So
if you want users to be able to add and edit datasets you have to configure
this using CKAN's user management. E.g.:

    sudo  -u ckanstd /var/lib/ckan/std/pyenv/bin/python /usr/bin/paster  --plugin=ckan rights make userxyz editor system  --config=/etc/ckan/std/std.ini


