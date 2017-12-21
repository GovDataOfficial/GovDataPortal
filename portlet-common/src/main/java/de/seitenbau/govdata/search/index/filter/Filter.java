package de.seitenbau.govdata.search.index.filter;

import com.liferay.portal.kernel.search.Document;

/**
 * Interface für die Filer.
 * 
 * @author rnoerenberg
 *
 */
public interface Filter
{
  /**
   * Determines whether a document is relevant for the index
   * @param document The document to check
   * @return true if document is relevant, otherwise false
   */
  public Boolean isRelevantForIndex(Document document);
}
