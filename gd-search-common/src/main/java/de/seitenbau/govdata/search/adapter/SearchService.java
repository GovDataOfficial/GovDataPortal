package de.seitenbau.govdata.search.adapter;

import java.util.List;

import org.elasticsearch.search.SearchHits;

import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.common.SearchQuery;
import de.seitenbau.govdata.search.index.model.SearchResultContainer;
import de.seitenbau.govdata.search.sort.Sort;

/**
 * Interface für den SuchService.
 * 
 * @author rnoerenberg
 *
 */
public interface SearchService
{
  /**
   * Führt eine Suche anhand der übergebenen Suchparameter aus.
   * 
   * @param q der Suchstring.
   * @param numResults die Anzahl der Resultate.
   * @param searchFilterBundle das Bundle mit den Suchfiltern.
   * @param sort die Sortierung.
   * @return das Ergebnis der Suche.
   */
  public SearchResultContainer search(
      SearchQuery q, Integer numResults, SearchFilterBundle searchFilterBundle, Sort sort);
  
  /**
   * Gibt die neuesten Einträge zum angegebenen Typ zurück. Wenn der Typ null ist wird nicht
   * gefiltert.
   * @param numResults
   * @param typeFilter
   * @return
   */
  public SearchResultContainer searchLatest(Integer numResults, String typeFilter);

  /**
   * Versucht anhand der übergebenen ScrollId weitere Treffer zu finden. Ist die ScrollId nicht mehr
   * vorhanden oder gültig, wird eine neue Suche gestartet.
   * 
   * @param scrollId die ScrollId, zu der die weiteren Treffer geladen werden sollen.
   * @return das Ergebnis der Suche.
   */
  public SearchResultContainer scroll(String scrollId);

  /**
   * Liefert anhand der übergebenen ScrollId die weiteren Treffer.
   * 
   * @param scrollId die ScrollId, zu der die weiteren Treffer geladen werden sollen.
   * @return das Ergebnis der Suche.
   */
  public SearchResultContainer scrollNextHits(String scrollId);

  /**
   * Lifert die Datensätze zu den übergebenen Identifiern.
   * 
   * @param ids
   * @param searchIndexes
   * @return
   */
  public SearchResultContainer singleSearch(List<String> ids, String[] searchIndexes);

  /**
   * Sucht die ContentIds zu der übergebenen PortletId.
   * 
   * @param portletId die PortletId.
   * @return eine Liste der ContentIds.
   */
  public List<String> findPortalContentIdsByPortletId(String portletId);
  
  /**
   * Lists some completions for the submitted q.
   * Splitting and recombining of words in the query has to be done in the frontend.
   * @param q last word of the query sentence
   * @return completions for q (only that term)
   */
  public List<String> findSearchAsYouTypeSuggestions(String q);

  /**
   * Get a List of Formats from the elastic search index, ordered by number of documents with that format.
   * @param limit the amount of formats to get
   * @return List of Formats
   */
  public List<String> getResourceFormats(int limit);
  
  /**
   * Record the phrase that has been searched to an elastic search index.
   * @param phrase
   */
  public void recordSearchPhrase(String phrase);

  /**
   * Get the metrics for the metadata-quality portlet. Uses metrics index.
   * @return Json response from elastic-search
   */
  public SearchHits getMetrics();

  /**
   * Get the numbers for gd-search.
   * @return Json response from elastic-search
   */
  public SearchResultContainer getNumbers();
}
