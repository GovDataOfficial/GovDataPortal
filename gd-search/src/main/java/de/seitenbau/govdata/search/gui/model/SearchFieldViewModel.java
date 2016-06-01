package de.seitenbau.govdata.search.gui.model;

import java.util.List;

import com.liferay.portal.kernel.util.KeyValuePair;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchFieldViewModel
{
  private String q;
  
  private List<KeyValuePair> passthroughParams;
  
  private String actionUrl;
  
  private String extendedSearchUrl;
  
  private String saytCompletionUrl;
  
  // Used to decide which version of the header should be rendered
  private Boolean showBigHeader;
  
  // show the search form?
  private Boolean showSearch;
  
  // needed in small Header version: background-image tintet in the color of the section
  private String backgroundImage;
}
