package de.seitenbau.govdata.search.gui.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterViewListModel extends ArrayList<FilterViewModel> {
  private static final long serialVersionUID = -1302064971452849865L;
  
  // can there only be one selected option?
  private boolean singletonFiltergroup;

}
