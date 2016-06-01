package de.seitenbau.govdata.search.index.mapper;

import java.util.HashMap;

public class ClassToTypeMapper extends HashMap<String, String>
{
  public ClassToTypeMapper()
  {
    put("com.liferay.portlet.blogs.model.BlogsEntry", "blog");
    put("com.liferay.portlet.journal.model.JournalArticle", "article");
  }
  
  public String getTypeForClass(String className)
  {
    if (containsKey(className))
    {
      return get(className);
    }
    return "";
  }
}
