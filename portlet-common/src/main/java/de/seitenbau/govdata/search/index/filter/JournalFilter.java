package de.seitenbau.govdata.search.index.filter;

import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.search.Document;

import de.seitenbau.govdata.search.index.LiferayDocumentFields;

/**
 * Filtert Liferay-Artikel.
 * 
 * @author rnoerenberg
 *
 */
@Component
public class JournalFilter implements Filter
{

  @Override
  public Boolean isRelevantForIndex(Document document)
  {
    Boolean isHead = Boolean.parseBoolean(document.get(LiferayDocumentFields.FIELD_HEAD));
    Boolean isVisible = Boolean.parseBoolean(document.get(LiferayDocumentFields.FIELD_VISIBLE));
    return (isHead && isVisible);
  }

}
