package de.seitenbau.govdata.search.adapter;

import java.util.List;

import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.index.model.SearchResultContainer;
import de.seitenbau.govdata.search.sort.Sort;

public interface SearchService
{
  public SearchResultContainer search(
      String q, Integer numResults, SearchFilterBundle searchFilterBundle, Sort sort);
  
  public SearchResultContainer scroll(String scrollId);

  public SearchResultContainer scrollNextHits(String scrollId);

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
}
