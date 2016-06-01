package de.seitenbau.govdata.search.index.filter;

import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import de.seitenbau.govdata.search.common.DocumentFields;

@Component
public class BlogFilter implements Filter
{
  
  @Override
  public Boolean isRelevantForIndex(Document document)
  {
    Integer status = Integer.parseInt(document.get(Field.STATUS));
    Boolean isVisible = Boolean.parseBoolean(document.get(DocumentFields.FIELD_VISIBLE));
    return (status == 0 && isVisible);
  }

}
