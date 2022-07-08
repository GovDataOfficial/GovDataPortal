package de.seitenbau.govdata.search.index.mapper;

import java.util.HashMap;

import de.seitenbau.govdata.search.index.IndexConstants;

public class ClassToTypeMapper extends HashMap<String, String>
{
  private static final long serialVersionUID = -515667049799780370L;

  public ClassToTypeMapper()
  {
    put(IndexConstants.CLASS_NAME_BLOGS_ENTRY, "blog");
    put(IndexConstants.CLASS_NAME_JOURNAL_ARTICLE_ENTRY, "article");
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
