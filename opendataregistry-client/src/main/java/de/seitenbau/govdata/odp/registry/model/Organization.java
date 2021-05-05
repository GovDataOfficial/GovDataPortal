package de.seitenbau.govdata.odp.registry.model;

import java.util.List;

public interface Organization extends Comparable<Organization>
{
  String getId();
  
  String getName();
  
  String getDisplayName();
  
  String getTitle();

  List<String> getContributorIds();
}
