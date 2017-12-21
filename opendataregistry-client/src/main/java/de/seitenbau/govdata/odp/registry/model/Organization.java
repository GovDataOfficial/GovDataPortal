package de.seitenbau.govdata.odp.registry.model;

public interface Organization extends Comparable<Organization>
{
  String getId();
  
  String getName();
  
  String getDisplayName();
  
  String getTitle();
}
