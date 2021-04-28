package de.seitenbau.govdata.search.index.filter;

import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.search.Document;

import de.seitenbau.govdata.search.index.LiferayDocumentFields;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtert Liferay-Artikel.
 * 
 * @author rnoerenberg
 *
 */
@Component
@Slf4j
public class JournalFilter implements Filter
{

  @Override
  public Boolean isRelevantForIndex(Document document)
  {
    Boolean isHead = Boolean.parseBoolean(document.get(LiferayDocumentFields.FIELD_HEAD));
    Boolean isVisible = Boolean.parseBoolean(document.get(LiferayDocumentFields.FIELD_VISIBLE));
    log.debug("isHead={};isVisible={}", isHead, isVisible);
    return (isHead && isVisible);
  }

}
