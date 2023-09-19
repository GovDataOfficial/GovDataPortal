# Changelog

## v6.2.1 2023-09-13

* Fixes icons after Font Awesome update

## v6.2.0 2023-09-13

* Replaces Twitter icon with the new X icon and removes the Facebook social media link
* Adds the possiblity to the portlet "GovData in numbers" additionally displaying the latest post from Mastodon
* Updates the Font Awesome library to the latest Font Awesome version 6.4.2 and disables the version shipped with Liferay
* Updates Apache Jena Fuseki client library to version 4.9.0
* Fixes interpreting line breaks as HTML on metadata details view
* Fixes navigating to type filter "all" on search result pages /daten and /showroom
* Improves the styling in headlines if the word is too long for the line

## v6.1.1 2023-08-01

* Updates the search criterias for the state search portlet

## v6.1.0 2023-08-01

* Supports using session ID for restricted BKG services. Replaces the use of the API Key with the session ID
  in the GovData map search.
* Fixes editing portlet information in the frontend configuration, if a portlet has JavaScript files included
* Improves reading and searching showcases from the showcases service
* Shows publisher filter options on search result page only if the type is available
* Adds db pool parameters and sets keepaliveTime and maxlifetime in portal configuration
* Sets individual keep alive strategy for HTTP client connections with a maximum keep alive time
* Sets connect and socket timeouts for HTTP client connections explicitly
* Increases max connections for the HTTP clients
* Changes slf4j dependency from version 2.x back to version 1.7.x, due to comatibility with Liferay

## v5.16.0 2023-06-29

* Updates maven dependencies to the latest bugfix / security fix version
* Improves counting over all types on the search results page when no type is selected
* Updates the search criterias for the state search portlet

## v5.15.0 2023-05-30

* Updates the search criterias for the state search portlet

## v5.14.0 2023-05-25

* Improves display of licences in the metadata details view

## v5.13.0 2023-05-04

* Adds licence attributes 'od_conformance' and 'osd_conformance'
* Improves code quality (e.g. by fixing SpotBugs warnings) and code style
* Fixes a problem that prevents the spatial search from being used in the state search
* Improves the relevance sorting for the state search

## v5.12.0 2023-04-13

* Now using CKAN API tokens instead of CKAN API keys. The CKAN API key support will be removed with CKAN 2.10.
* Unused legacy code in the library 'opendataregistry-client' was removed
* Updated README.md and INSTALL.md
* Add jacoco maven plugin (test coverage) to build process

## v5.11.0 2023-03-16

* Adds possibility to define the property `layers` for the map search by configuration. Switches the map
  service in the portal GovData from "WebAtlasDE" to "BaseMapDE".
* Fixes managing tags and categories in web content
* Adds the possibility to disable the contributorID in the metadata edit form. When the contributorID is
  deactivated by setting `editform.disable.contributor.id.field=true`, the field is no longer added in the
  edit form.
* Prevents adding the type checklist in the extended search if the type filter is "all"

## v5.10.0 2023-03-06

* Adds a search filter for high-value datasets based on a search for specific tags
* Support for favicons in several dimensions on different systems and browsers
* Adds possibility to deactivate showcase and state search
* Optimizes size of the logo
* Updates maven dependencies to the latest bugfix / security fix version
* Updates to Elasticsearch 7.17.9
* Improves the creation of internal search links
* Improves error handling when creating internal links

## v5.9.0 2023-01-23

* Updates Apache Jena Fuseki client library to version 4.7.0

## v5.8.1 2022-12-22

* Improves the user interface of the SPARQL editor for mobile clients

## v5.8.0 2022-12-15

* Adds support for dcat:accessService in Distribution to the search and the details view
* Writes modified date in resources into field `modified` now

## v5.7.0 2022-11-24

* Reduces the blacklisted liferay components
* Supports Liferay version 7.4.3.41 GA41 now
* Fixes URLs in info mails from the blog module: Generates URLs without port if no port is configured

## v5.6.0 2022-11-03

* Updates maven dependencies to the latest bugfix / security fix version

## v5.5.0 2022-10-20

* Overwrites pop-up template in govdatastyle-theme to include JQuery in IFrame
* Updates the search criterias for the state search portlet
* Adds support for dcatap:availability in Distribution to the metadata edit form and details view
* Displays the availability text instead of the URI for the distributions in the metadata details view
* Fixes text for TOP 5 formats in the metadata quality dashboard

## v5.4.0 2022-09-12

* Internal release: Updating deployment scripts

## v5.2.2 2022-07-19

* Updates maven dependencies to the latest bugfix / security fix version

## v5.2.0 2022-05-31

* Adds support for Java 11
* Updates Apache Jena Fuseki client library to version 4.5.0 (requires Java 11)
* Fixes error handling when fetching metrics data and no data is present
* Updates Spring dependencies and switches from "spring-webmvc-portlet" to "com.liferay.portletmvc4spring.framework"
* Updates maven dependencies to the latest bugfix / security fix version

## v5.1.2 2022-05-05

* Fixes bug when fetching metrics data from Elasticsearch

## v5.1.1 2022-04-13

* Fixes writing additional information about resources with CKAN 2.9 when saving datasets using the metadata edit form

## v5.0.0 2022-03-24

* Updates to Elasticsearch 7.16.x
* Moves microservices and index-updater to separate repository
* Removes individual python scripts

## v4.7.0 2022-02-17

* Improves date deserialization when reading metadata from CKAN
* Saves ContributorID in addition to the organizationID in the triple store with the validation results
* Do not include ContributorID in discoverable metrics anymore as it is always added now
* Adds selection of a triplestore dataset which contains the SHACL validation results to the sparql editor
* Adds description for the usability chart in the metadata quality dashboard
* Updates dependencies log4j and slf4j to the latest versions

## v4.6.5 2022-01-18

* Updates the search criterias for the state search portlet

## v4.6.4 2021-12-21

* Updates maven dependencies Log4J to the latest security fix version 2.17.0

## v4.6.3 2021-12-16

* Updates maven dependencies to the latest bugfix / security fix version
* Allows showcase contact mail and url to be stored without a name
* Fixes bug when changing primary showcase type to an already saved additional type
* Fixes showcase database unique constraints for the tables `showcase_link`, `showcase_used_dataset` and `showcase_image`

## v4.6.0 2021-11-04

* A dataset is now deleted from the triplestore when switching from public to private in the metadata edit form
* Adds CSS styles for new horizontal sub navigation bar
* Fixes blog navigation bar switching between public and draft blog entries
* Adds the possiblity to the portlet "GovData in numbers" additionally displaying the latest tweet from twitter
* Introduces new "state search" portlet
* Little style improvements and new background images in the search bar
* Introduces new "featured datasets" portlet
* Introduces new "featured showcase" portlet
* Introduces new portlet for displaying "GovData in numbers", e.g. counting datasets, apps and blog entries

## v4.5.5 2021-09-20

* Fixes default sort for map searches without search phrase by changing it back to 'relevance'

## v4.5.3 2021-09-02

* Updates jsoup version
* Do not show private showcases in metadata details view

## v4.5.2 2021-07-27

* Installation documentation updated
* Improves licence cache in module 'opendataregistry-client'
* Increases version of some maven dependencies (bugfixes / security fixes)
* Enforces 3.6.0 as minimum maven version

## v4.5.0 2021-07-15

* Fixes maven build of the project from scratch (#5)
* Adds information on metadata details view: contributor name and license attribution by text
* Fix checkstyle violations and excludes some classes and files
* Sort search results by last modification date by default when no search phrase is given
* Introduces application 'index-updater' for reindexing showcases in the search index
* Searches additionally in field 'metadata.publisher_name'
* Allows `<b>, <i>, <u>` and `<br>` tags in notes
* Viewing showcases in the search results
* Adds information on the metadata detail view about related showcases
* Introduces detail view of showcases
* Introduces new portlet plugin for editing showcases
* Adds a new microservice for storing data in a separate database

## v4.4.0 2021-03-26

* Adds the DCAT-AP.de property "contributorID" to the edit form
* Introduce the possiblity to validate the dataset graph by SHACL when updating the dataset and save the
  validation result in a triplestore
* Introduce new metadata quality portlet

## v4.3.5 2021-03-03

* Replaced Captcha with honeypot
* Disables editing screen name for all users except administrators

## v4.3.3 2021-02-09

* Add checkbox for privacy policy agreement to create account and create guest account forms
* Add custom screen name generator and move screen name validator into common screenname-hook

## v4.3.2 2021-01-28

* Switch search field in extended search for type publisher

## v4.3.1 2021-01-22

* Fix possible XSS security vulnerability in site template
* Extract Elasticsearch related packages to new module gd-search-common

## v4.3.0 2021-01-19

* Introduce new developer corner portlet with SPARQL editor
* Update triple store when metadata has been edited

## v4.1.0 2020-11-06

* Anonymize user name in comments posted by guests

## v4.0.0 2020-10-30

* Update to Liferay 7 (e.g. switching to OSGI bundles and adapting GovData theme)
* Removed cache-scheduler in favor of setting a TTL for the cache entries
* Removed errorpages-hook in favor of an custom error page handling in the web server
* Add new standalone maintenance html page

## v3.8.0 2020-02-17

* Introduce minimum length for saving a search phrase
* Add SpotBugs Maven Plugin

## v3.7.0 2019-12-19

* Adapt CKAN binding to new CKAN version 2.8.3
* Disable rating functionality, because the new CKAN version does not provide the required information anymore
* Add Python script for cleaning Elasticsearch index
* Increase upload max file size from 10 up to 100 MB

## v3.6.2 2019-11-28

* Integrate Schema.org representation in the HTML source code of the metadata detail page for indexing by Google Dataset Search
* Fix problem with invalid web addresses in resource links on the metadata detail page

## v3.6.0 2019-11-05

* Resolve display name for older DCAT-AP.de licenses

## v3.5.0 2019-07-13

* Replace distribution description with distribution title
* Display distribution description below title/url if available

## v3.2.2 2018-12-14

* Use field `metadata.type` instead of `type` for typefilter in the elasticsearch request

## v3.2.0 2018-11-29

* Also shorten EU file type URLs without `/mdr/`

## v3.1.3 2018-11-09

* Display the value in dct:publisher as "Veröffentlichende Stelle" in detail view of the metadata
* Pre-select default license in the metadata edit form
* Re-activate Link-Checker by adding report site in Liferay menu again

## v3.1.2 2018-06-01

* Updated tests to DCAT-AP.de 1.0.1
* Fixed error when no license information in resource is given

## v3.1.1 2018-04-27

* Fix comment field title
* Add config param lucene.replicate.write for activating search index replication in cluster mode

## v3.0.1 2017-12-21

* Migrated from OGD to DCAT-AP.de

## v2.4.6 2017-06-23

* Restricted access to the JSON Web Service API
* The caches for categories and licences now expires after some time

## v2.4.4 2016-11-18

* Fixed little user interface bug in Internet Explorer
* Improved logging when a UnknownRoleException occurred in parsing metadata contacts
* Fixed some checkstyle violations

## v2.4.1 2016-08-15

* Support for Liferay 6.2.5 GA6
* Support for CKAN 2.5.x
* Added credits to search map

## v2.3.0 2016-06-01

* Initial commit "Regelbetrieb" (Version 2.3.0)