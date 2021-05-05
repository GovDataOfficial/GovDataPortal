package de.seitenbau.govdata.fuseki;

/**
 * Das Interface für den Fuseki Client.
 * Der Client wird verwendet um mit dem Triplestore zu kommunizieren.
 * 
 */
public interface FusekiClient
{
  /**
   * Sucht den Datensatz mit dem angegebenen identifier im Triplestore und löscht ihn
   * @param ckanDatasetBaseUrl Die URL zu Ckan
   * @param ckanTechnicalId Der Ckan-Identifier des Datensatzes
   * @param identifier Der Metadata-Identifier des Datensatzes
   */
  void deleteDataset(String ckanDatasetBaseUrl, String ckanTechnicalId, String identifier);
  
  /**
   * Aktualisiert den Datensatz mit dem angegebenen identifier im Triplestore. Falls kein Datensatz
   * vorhanden ist wird ein neuer angelegt.
   * @param ckanDatasetBaseUrl Die URL zu Ckan
   * @param ckanTechnicalId Der Ckan-Identifier des Datensatzes
   * @param identifier Der Metadata-Identifier des Datensatzes
   */
  void updateOrCreateDataset(String ckanDatasetBaseUrl, String ckanTechnicalId, String identifier);
}
