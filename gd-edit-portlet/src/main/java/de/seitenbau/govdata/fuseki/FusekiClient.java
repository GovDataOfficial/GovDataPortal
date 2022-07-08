package de.seitenbau.govdata.fuseki;

import de.seitenbau.govdata.odp.registry.model.User;

/**
 * Das Interface für den Fuseki Client.
 * Der Client wird verwendet um mit dem Triplestore zu kommunizieren.
 * 
 */
public interface FusekiClient
{
  /**
   * Sucht den Datensatz mit dem angegebenen identifier im Triplestore und löscht ihn
   * @param user Ckan API User
   * @param ckanTechnicalId Der Ckan-Identifier des Datensatzes
   * @param identifier Der Metadata-Identifier des Datensatzes
   */
  void deleteDataset(User user, String ckanTechnicalId, String identifier);
  
  /**
   * Aktualisiert den Datensatz mit dem angegebenen identifier im Triplestore. Falls kein Datensatz
   * vorhanden ist wird ein neuer angelegt.
   * @param user Ckan API User
   * @param ckanTechnicalId Der Ckan-Identifier des Datensatzes
   * @param identifier Der Metadata-Identifier des Datensatzes
   * @param ownerOrgId Id des Datenbereitstellers
   * @param contributorId contributorId zum Datensatz
   */
  void updateOrCreateDataset(User user, String ckanTechnicalId, String identifier,
      String ownerOrgId, String contributorId);
}
