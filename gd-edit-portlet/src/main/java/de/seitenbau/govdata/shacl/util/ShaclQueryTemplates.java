package de.seitenbau.govdata.shacl.util;

/**
 * Enthält die Templates für Querys die der SHACL Validator verwendet
 *
 */
public abstract class ShaclQueryTemplates
{

  /**
   * Report Query
   * @param %s Insert URI of the dataset
   * @param %s: Insert ID of the owner organization
   */
  private static final String REPORT_QUERY = "PREFIX sh: <http://www.w3.org/ns/shacl#> " +
      "PREFIX dqv: <http://www.w3.org/ns/dqv#> " +
      "PREFIX govdata: <http://govdata.de/mqa/#> " +
      "CONSTRUCT { " +
      "    ?report dqv:computedOn <%s> . " +
      "    ?report govdata:attributedTo '%s' . " +
      "    ?s ?p ?o . " +
      "} WHERE { " +
      "    { ?report a sh:ValidationReport . } " +
      "    UNION " +
      "    { ?s ?p ?o . }" +
      "}";

  /**
   * @param datasetUri Datensatz URI
   * @param ownerOrg Organisations ID
   * @return
   */
  public static String getReportQuery(String datasetUri, String ownerOrg)
  {
    return String.format(REPORT_QUERY, datasetUri, ownerOrg);
  }
}
