package de.seitenbau.govdata.search.index.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterListDto extends ArrayList<FacetDto> {
  private static final long serialVersionUID = -1713236388757569126L;
  
  // can there only be one selected option?
  private boolean singletonFiltergroup;

}
