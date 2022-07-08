package de.seitenbau.govdata.shacl;

import org.apache.jena.rdf.model.Model;

/**
 * Das Interface für den SHACL Validator.
 * Der Client wird verwendet um mit dem SHACL Endpunkt zu kommunizieren.
 * 
 */
public interface ShaclValidator
{

  /**
   * Validiert den Datensatz mit dem SHACL Validator
   * @param uri URI des Datensatzes
   * @param model Der Datensatz als RDF Model
   * @param ownerOrgId Id des Datenbetreitstellers
   * @param contributorId contributorId zum Datensatz
   * @return
   */
  Model validate(String uri, Model model, String ownerOrgId, String contributorId);

  /**
   * Prüft ob der SHACL-Validator erreichbar ist
   * @return
   */
  boolean isAvailable();
}