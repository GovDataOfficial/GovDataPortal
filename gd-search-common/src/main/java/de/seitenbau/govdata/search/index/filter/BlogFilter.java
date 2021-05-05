package de.seitenbau.govdata.search.index.filter;

import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import de.seitenbau.govdata.search.index.LiferayDocumentFields;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtert Blogeinträge.
 * 
 * @author rnoerenberg
 *
 */
@Component
@Slf4j
public class BlogFilter implements Filter
{
  
  @Override
  public Boolean isRelevantForIndex(Document document)
  {
    Integer status = Integer.parseInt(document.get(Field.STATUS));
    Boolean isVisible = Boolean.parseBoolean(document.get(LiferayDocumentFields.FIELD_VISIBLE));
    log.debug("status={};isVisible={}", status, isVisible);
    return (status == 0 && isVisible);
  }

}
