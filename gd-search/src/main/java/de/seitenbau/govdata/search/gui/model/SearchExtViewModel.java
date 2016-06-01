package de.seitenbau.govdata.search.gui.model;

import java.util.List;
import java.util.Map;

import com.liferay.portal.kernel.util.KeyValuePair;

import de.seitenbau.govdata.search.common.searchresult.PreparedParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchExtViewModel
{
  private PreparedParameters preparedParameters;
  
  private List<Map<String, String>> categoryList;
  
  private List<Map<String, String>> licenceList;
  
  private List<Map<String, String>> organizationList;
  
  private List<Map<String, String>> typeList;
  
  private List<Map<String, String>> formatList;
  
  private List<Map<String, String>> isopenList;
  
  private Map<String, String> translationMap;
  
  private List<KeyValuePair> passthroughParams;
  
  private String[] hiddenFields;
  
  private String actionUrl;
}

