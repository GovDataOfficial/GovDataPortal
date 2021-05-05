
function getConfig() {
  return {
    prefixes: {
     "adms":    "http://www.w3.org/ns/adms#",
     "dcat":    "http://www.w3.org/ns/dcat#",
     "dcatde":  "http://dcat-ap.de/def/dcatde/",
     "dct":     "http://purl.org/dc/terms/",
     "foaf":    "http://xmlns.com/foaf/0.1/",
     "geo":     "http://www.w3.org/2003/01/ge",
     "geof":    "http://www.opengis.net/def/function/geosparql/",
     "rdf":     "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
     "uom":     "http://www.opengis.net/def/uom/OGC/1.0/",
     "xsd":     "http://www.w3.org/2001/XMLSchema#"
    },
    queries: [
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
      }
    ]
  };
}

function loadExample(id) {
  var config = getConfig();
  var queryPrefixes = config.queries[id].prefixes || [];
  
  // set query
  yasqe.setValue(config.queries[id].query);
  
  // set prefixes for query and sync prefix-buttons
  for (var key in config.prefixes) {
    var prefixBtnElement = document.getElementById("prefixButton_" + key);
    if (queryPrefixes.indexOf(key) >= 0) {
      if (!prefixBtnElement.classList.contains("prefix-btn-active")) {
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

function runQuery() {
  var tab = yasgui.getTab();
  tab.query();
}