package de.seitenbau.govdata.search.index.filter;

import java.util.HashMap;
import java.util.Map;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

/**
 * Ruft die Filter auf, die die für den Suchindex relevanten Einträge rausfiltern.
 * 
 * @author rnoerenberg
 *
 */
public class FilterProxy implements Filter
{
  private Map<String, Filter> registeredFilter;
  
  /**
   * Set the filter map. Maps Field.ENTRY_CLASS_NAME to a specific index filter
   * @param filter
   */
  public void setRegisteredFilter(Map<String, Filter> filter)
  {
    if (filter != null)
    {
      registeredFilter = new HashMap<String, Filter>(filter);
    }
  }
  
  @Override
  public Boolean isRelevantForIndex(Document document)
  {
    String documentType = document.get(Field.ENTRY_CLASS_NAME);
    
    if (!registeredFilter.containsKey(documentType))
    {
      return false;
    }
    return registeredFilter.get(documentType).isRelevantForIndex(document);
  }
}
