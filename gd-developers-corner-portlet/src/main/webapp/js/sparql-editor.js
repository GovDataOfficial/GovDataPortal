
function getConfig() {
  return {
    prefixes: {
     "adms":    "http://www.w3.org/ns/adms#",
     "dcat":    "http://www.w3.org/ns/dcat#",
     "dcatde":  "http://dcat-ap.de/def/dcatde/",
     "dct":     "http://purl.org/dc/terms/",
     "dqv":     "http://www.w3.org/ns/dqv#",
     "foaf":    "http://xmlns.com/foaf/0.1/",
     "geo":     "http://www.w3.org/2003/01/ge",
     "geof":    "http://www.opengis.net/def/function/geosparql/",
     "mqa":     "http://govdata.de/mqa/#",
     "rdf":     "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
     "shacl":   "http://www.w3.org/ns/shacl#",
     "uom":     "http://www.opengis.net/def/uom/OGC/1.0/",
     "xsd":     "http://www.w3.org/2001/XMLSchema#"
    },
    queries: {
      "ds": [
        { "name": "Anzahl der Datensätze insgesamt",
          "query": "SELECT (COUNT(?dataset) AS ?datasets) WHERE {\n" +
                   "  ?dataset a dcat:Dataset .\n" +
                   "}",
          "prefixes": ["dcat"]
        },
        { "name": "Durchschnittliche Anzahl Distributionen pro Datensatz",
          "query": "SELECT ?datasets ?distributions (?distributions / ?datasets AS ?averageDistributionsPerDataset) WHERE {\n" +
                   "  {\n" +
                   "  SELECT (COUNT(?dataset) AS ?datasets) (SUM(?distributionsPerDataset) AS ?distributions) WHERE {\n" +
                   "      {\n" +
                   "      SELECT ?dataset (COUNT(?distribution) AS ?distributionsPerDataset) WHERE {\n" +
                   "        ?dataset a dcat:Dataset .\n" +
                   "        ?dataset dcat:distribution ?distribution .\n" +
                   "        } GROUP BY ?dataset\n" +
                   "      }\n" +
                   "    }\n" +
                   "  }\n" +
                   "}\n" +
                   "LIMIT 100",
          "prefixes": ["dcat"]
        },
        { "name": "Schlagwörter oder Kategorien der letzten 30 Tage, sortiert nach Aufkommen",
          "query": "SELECT DISTINCT ?keyword (COUNT(?keyword) AS ?count) WHERE {\n" +
                   "  SELECT ?keyword WHERE {\n" +
                   "      ?dataset a dcat:Dataset .\n" +
                   "      ?dataset dct:modified ?modified .\n" +
                   "      BIND(DATATYPE(?modified) AS ?modifiedType) .\n" +
                   "      FILTER(?modifiedType IN (xsd:dateTime, xsd:date)) .\n" +
                   "      BIND(NOW() as ?nowDateTime) .\n" +
                   "      BIND(xsd:date(?nowDateTime) AS ?nowDate) .\n" +
                   "      BIND(\"P30DT0H0M0.000S\"^^xsd:duration AS ?timespan) .\n" +
                   "      BIND((?nowDateTime - ?timespan) AS ?earliestDateTime) .\n" +
                   "      BIND((?nowDate - ?timespan) AS ?earliestDate) .\n" +
                   "      BIND(IF(?modifiedType = xsd:dateTime, ?modified > ?earliestDateTime, ?modified > ?earliestDate) AS ?isWithinTimespan) .\n" +
                   "      FILTER(?isWithinTimespan = true) .\n" +
                   "      ?dataset dcat:keyword ?keyword .\n" +
                   "  }\n" +
                   "} GROUP BY ?keyword\n" +
                   "  ORDER BY DESC(?count)\n" +
                   "LIMIT 100",
          "prefixes": ["dcat", "dct", "xsd"]
        },
        { "name": "Anzahl geteilter Kategorien und Schlagwörter mit anderen Datensätzen",
          "query": "SELECT ?dataset (COUNT(DISTINCT ?theme) AS ?sharedThemes) (COUNT(DISTINCT ?keyword) AS ?sharedKeywords) WHERE {\n" +
                   "    # hier wird ein Beispieldatensatz verwendet, der sich für die Untersuchung anderer Datensätze beliebig austauschen lässt\n" +
                   "    # \"Geologische Übersichtskarte der Bundesrepublik Deutschland 1:200.000 (GÜK200) - CC 8742 Bad Reichenhall\"\n" +
                   "    BIND(<https://gdk.gdi-de.org/inspire/srv/eng/xml_iso19139?uuid=03158696-2C79-48E3-9C61-D205DFA94690> AS ?fixedDataset) .\n" +
                   "    ?fixedDataset dcat:theme ?theme .\n" +
                   "    ?fixedDataset dcat:keyword ?keyword .\n" +
                   "    FILTER(STR(?keyword) != 'opendata') .\n" +
                   "    ?dataset a dcat:Dataset .\n" +
                   "    FILTER(?fixedDataset != ?dataset) .\n" +
                   "    ?dataset dcat:keyword ?keyword .\n" +
                   "    ?dataset dcat:theme ?theme .\n" +
                   "} GROUP BY ?dataset\n" +
                   "LIMIT 100",
          "prefixes": ["dcat"]
        },
        { "name": "Zeitliche Nähe zu anderen Datensätzen",
          "query": "SELECT ?dataset ?dayDiff WHERE {\n" +
                   "    # hier wird ein Beispieldatensatz verwendet, der sich für die Untersuchung anderer Datensätze beliebig austauschen lässt\n" +
                   "    # \"Geologische Übersichtskarte der Bundesrepublik Deutschland 1:200.000 (GÜK200) - CC 8742 Bad Reichenhall\"\n" +
                   "    BIND(<https://gdk.gdi-de.org/inspire/srv/eng/xml_iso19139?uuid=03158696-2C79-48E3-9C61-D205DFA94690> AS ?fixedDataset) .\n" +
                   "    ?dataset a dcat:Dataset .\n" +
                   "    FILTER(?fixedDataset != ?dataset) .\n" +
                   "    ?dataset dct:modified ?modified .\n" +
                   "    FILTER(DATATYPE(?modified) IN (xsd:dateTime, xsd:date)) .\n" +
                   "    ?fixedDataset dct:modified ?fixedModified .\n" +
                   "    BIND(ABS(\n" +
                   "        ((YEAR(?modified) - YEAR(?fixedModified)) * 365) +\n" +
                   "        ((MONTH(?modified) - MONTH(?fixedModified)) * 30) +\n" +
                   "        (DAY(?modified) - DAY(?fixedModified))\n" +
                   "    ) AS ?dayDiff) .\n" +
                   "} GROUP BY ?dataset ?dayDiff\n" +
                   "  ORDER BY ASC(?dayDiff)\n" +
                   "LIMIT 100",
          "prefixes": ["dcat", "dct", "xsd"]
        },
        { "name": "Titel von Datensätzen",
          "query": "SELECT ?uri ?title ?contributorid WHERE {\n" +
                   "    ?uri dct:title ?title .\n" +
                   "    # hier werden URIs von Beispieldatensätzen verwendet, die sich mit beliebigen URIs austauschen lassen\n" +
                   "    FILTER(?uri IN(<https://opendata.potsdam.de/api/v2/catalog/datasets/obm-2018>, <https://www.offenedaten-wuppertal.de/dataset/fl%C3%A4chennutzungsplan-wuppertal-stand-17012005>, <https://ckan.govdata.de/dataset/0f79531a-e808-5d4c-9d59-a4268a807ae0>))\n" +
                   "    ?uri dcatde:contributorID ?contributorid\n" +
                   "    FILTER(isURI(?contributorid))\n" +
                   "    FILTER(strstarts(str(?contributorid), \"http://dcat-ap.de/def/contributors/\"))\n" +
                   "}",
          "prefixes": ["dcatde", "dct"]
        }
      ],
      "mqa": [
        { "name": "Anzahl der Validierungsergebnisse nach ContributorID",
          "query": "SELECT ?contributor (COUNT(?report) as ?count)\n" +
                   "WHERE {\n" +
                   "    ?report rdf:type shacl:ValidationReport .\n" +
                   "    ?report mqa:attributedTo ?contributor .\n" +
                   "    FILTER(isURI(?contributor))\n" +
                   "}\n" +
                   "GROUP BY ?contributor\n" +
                   "ORDER BY DESC (?count)",
          "prefixes": ["mqa", "rdf", "shacl"]
        },
        { "name": "Validierungsergebnisse nach Indikator sortiert",
          "query": "SELECT ?contributor ?metric (COUNT(?metric) as ?count)\n" +
                   "WHERE {\n" +
                   "    ?report rdf:type shacl:ValidationReport .\n" +
                   "    ?report mqa:attributedTo ?contributor .\n" +
                   "    FILTER(isURI(?contributor))\n" +
                   "    ?report shacl:result ?s .\n" +
                   "    ?s shacl:resultSeverity ?metric\n" +
                   "    FILTER(!contains(str(?metric), 'count_'))\n" +
                   "}\n" +
                   "GROUP BY ?contributor ?metric\n" +
                   "ORDER BY ASC (?metric) DESC(?count)",
          "prefixes": ["mqa", "rdf", "shacl"]
        },
        { "name": "URIs aller Datensätze die kein Keyword enthalten",
          "query": "SELECT DISTINCT ?uri\n" +
                   "WHERE {\n" +
                   "    ?report rdf:type shacl:ValidationReport .\n" +
                   "    ?report shacl:result  ?result .\n" +
                   "    # Die Ergebnisse können pro Datenbereitsteller gefiltert werden, hier am Beispiel des Transparenzportals Bremen. Alle möglichen Werte für die ContributorID finden Sie unter: https://www.dcat-ap.de/def/contributors/\n" +
                   "    # Hierfür einfach das Hashzeichen in der nachfolgenden Zeile entfernen, um den Filter zu aktivieren.\n" +
                   "    #?report mqa:attributedTo <http://dcat-ap.de/def/contributors/transparenzportalBremen> .\n" +
                   "    ?result shacl:resultSeverity mqa:no_literal_keyword .\n" +
                   "    ?report dqv:computedOn ?uri\n" +
                   "} LIMIT 500",
          "prefixes": ["dqv", "mqa", "rdf", "shacl"]
        },
        { "name": "URIs aller Datensätze die keine offene Lizenz enthalten",
          "query": "SELECT DISTINCT ?uri\n" +
                   "WHERE {\n" +
                   "    ?report rdf:type shacl:ValidationReport .\n" +
                   "    ?report shacl:result  ?result .\n" +
                   "    # Die Ergebnisse können pro Datenbereitsteller gefiltert werden, hier am Beispiel von Open.NRW. Alle möglichen Werte für die ContributorID finden Sie unter: https://www.dcat-ap.de/def/contributors/\n" +
                   "    # Hierfür einfach das Hashzeichen in der nachfolgenden Zeile entfernen, um den Filter zu aktivieren.\n" +
                   "    #?report mqa:attributedTo <http://dcat-ap.de/def/contributors/openNRW> .\n" +
                   "    ?result shacl:resultSeverity mqa:no_open_license_from_list .\n" +
                   "    ?report dqv:computedOn ?uri\n" +
                   "} LIMIT 500",
          "prefixes": ["dqv", "mqa", "rdf", "shacl"]
        }
      ]
    }
  };
}

var selectedEndpointType;
var availableEndpoints;
var previousEndpointType;
var cacheEndpointQuery = {};

function setSelectedEndpointType(id) {
  selectedEndpointType = id;
}

function setAvailableEndpoints(endpoints) {
  availableEndpoints = endpoints;
}

function getEndpointByUrl(url) {
  for (i in availableEndpoints) {
    if (availableEndpoints[i].url === url) {
      return availableEndpoints[i];
    }
  }
}

function loadExample(id) {
  var config = getConfig();
  var queryPrefixes;
  
  // set query
  if (selectedEndpointType) {
    yasqe.setValue(config.queries[selectedEndpointType][id].query);
    queryPrefixes = config.queries[selectedEndpointType][id].prefixes || [];
  } else {
    // unknown value
    return;
  }
  loadPrefixes(queryPrefixes);
}

function loadPrefixes(activePrefixes) {
  // set prefixes and sync prefix-buttons
  config = getConfig();
  for (var key in config.prefixes) {
    var prefixBtnElement = document.getElementById("prefixButton_" + key);
    if (activePrefixes.indexOf(key) >= 0) {
      if (!prefixBtnElement.classList.contains("prefix-btn-active")) {
          // set prefix as active
          prefixBtnElement.classList.add("prefix-btn-active");
        }
        var prefixObj = {};
        prefixObj[key] = config.prefixes[key];
        yasqe.addPrefixes(prefixObj);
        
    } else {
      if (prefixBtnElement.classList.contains("prefix-btn-active")) {
        prefixBtnElement.classList.remove("prefix-btn-active");
      }
    }
  }
}

function togglePrefix(prefixKey, prefixValue) {
  var prefixObj = {};
  prefixObj[prefixKey] = prefixValue;
  
  var prefixBtnElement = document.getElementById("prefixButton_" + prefixKey);
  if (prefixBtnElement.classList.contains("prefix-btn-active")) {
    prefixBtnElement.classList.remove("prefix-btn-active");
    yasqe.removePrefixes(prefixObj);
  } else {
    prefixBtnElement.classList.add("prefix-btn-active");
    yasqe.addPrefixes(prefixObj);
  }
}

function setContentType(value) {
  var tab = yasgui.getTab();
  tab.setRequestConfig({ acceptHeaderSelect: value });
  yasgui.config.requestConfig.acceptHeaderSelect = value;
}

function setEndpoint(endpoint) {
  // set the selected endpoint and store previous value
  var tab = yasgui.getTab();
  tab.setRequestConfig({ endpoint: endpoint.url });
  yasgui.config.requestConfig.endpoint = endpoint.url;
  // keep info about the new selected enpoint type
  setSelectedEndpointType(endpoint.type);
  // set queries for endpoint type
  if (setQueries(endpoint.type))
  {
    if (previousEndpointType) {
      // store previous query
      cacheEndpointQuery[previousEndpointType] = {
        "query": yasqe.getValue(), "prefixes": yasqe.getPrefixesFromQuery()};
    }
    if (cacheEndpointQuery[endpoint.type]) {
      // load stored query for the selected endpoint
      yasqe.setValue(cacheEndpointQuery[endpoint.type]["query"]);
      loadPrefixes(Object.keys(cacheEndpointQuery[endpoint.type]["prefixes"]));
    } else {
      // load first example for the selected endpoint as default value
      loadExample(0);
    }
  }
  // save current enpoint as previous endpoint for next switch of the endpoint
  setPreviousEndpointType(endpoint.type);
}

function setPreviousEndpointType(type) {
  previousEndpointType = type;
}

function setQueries(type) {
  queries = getConfig().queries[type];
  if (!queries) {
    // unknown value
    return false;
  }
  var listHtml = '<h2>Beispiel-Abfragen:</h2>';
  queries.forEach(function (q, i) {
    listHtml += '<a id="sparqlExample' + i + '" class="btn btn-margin" onclick="loadExample(' + i + ')">'
      + q.name + '</a>';
  });
  document.getElementById("exampleList").innerHTML = listHtml;
  return true;
}

function runQuery() {
  var tab = yasgui.getTab();
  tab.query();
}

function toggleExtText(icon, textBlockIdSelector) {
  var suffix = 'open';
  var titleLangKey = 'od.developers.corner.endpoint.show.less';
  if ($(textBlockIdSelector).is(":visible")) {
    suffix = 'closed';
    titleLangKey = 'od.developers.corner.endpoint.show.more';
    icon.innerHTML = getInfoIcon(suffix);
  } else {
    icon.innerHTML = getInfoIcon(suffix);
  }
  icon.title = Liferay.Language.get(titleLangKey);
  $(textBlockIdSelector).toggle();
}

function getInfoIcon(suffix) {
  return '<svg aria-hidden="true" class="lexicon-icon lexicon-icon-info-panel-' + suffix + '">' +
      '<use xlink:href="' + Liferay.ThemeDisplay.getPathThemeImages() + '/lexicon/icons.svg#info-panel-' + suffix + '" />' +
    '</svg>';
}