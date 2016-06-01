package de.fhg.fokus.odp.registry.model;

public interface Organization extends Comparable<Organization>
{
  String getId();
  
  String getName();
  
  String getDisplayName();
  
  String getTitle();
}
