package de.seitenbau.govdata.search.util.states;

import lombok.Data;

@Data
public class StateViewModel
{
  private int id;

  private String stateKey;

  private String displayName;

  private String name;

  private String url;

  private long resultCount;

  private String cssClass;

  private boolean blocked;
}
