# Changelog

## v3.8.0 2020-02-17

* Introduce minimum length for saving a search phrase
* Add SpotBugs Maven Plugin

## v3.7.0 2019-12-19

* Adapt CKAN binding to new CKAN version 2.8.3
* Disable rating functionality, because the new CKAN version does not provide the required information no more
* Add Python script for cleaning Elasticsearch index
* Increase upload max file size from 10 to 100 MB

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