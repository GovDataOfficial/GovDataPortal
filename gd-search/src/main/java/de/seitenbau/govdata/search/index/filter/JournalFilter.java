package de.seitenbau.govdata.search.index.filter;

import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.search.Document;

import de.seitenbau.govdata.search.common.DocumentFields;

@Component
public class JournalFilter implements Filter
{

  @Override
  public Boolean isRelevantForIndex(Document document)
  {
    Boolean isHead = Boolean.parseBoolean(document.get(DocumentFields.FIELD_HEAD));
    Boolean isVisible = Boolean.parseBoolean(document.get(DocumentFields.FIELD_VISIBLE));
    return (isHead && isVisible);
  }

}
