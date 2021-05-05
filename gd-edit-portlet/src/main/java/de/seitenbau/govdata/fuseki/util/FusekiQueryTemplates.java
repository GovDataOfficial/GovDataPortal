package de.seitenbau.govdata.fuseki.util;

/**
 * Enthält die Templates für Query-Anfragen an den Triplestore
 *
 */
public abstract class FusekiQueryTemplates
{

  /**
   * Delete Query
   * %s: Insert URI of the dataset
   */
  private static final String DELETE_DATASET_QEURY = "PREFIX dct: <http://purl.org/dc/terms/>\n"
      + "\n"
      + "DELETE { ?s ?p ?o }\n"
      + "WHERE {\n"
      + "  <%s> (<>|!<>)* ?s . \n"
      + "  FILTER NOT EXISTS { <%s> <http://purl.org/dc/terms/publisher> ?s }\n"
      + "  ?s ?p ?o .\n"
      + "}";

  /**
   * Gibt eine Query zum Löschen des Datensatzes mit der URI datasetUri. Diese Query muss an den
   * /update Endpukt des Triplestores geschickt werden.
   * @param datasetUri URI des Datensatzes
   * @return
   */
  public static String getDeleteDatasetQuery(String datasetUri)
  {
    return String.format(DELETE_DATASET_QEURY, datasetUri, datasetUri);
  }
}
