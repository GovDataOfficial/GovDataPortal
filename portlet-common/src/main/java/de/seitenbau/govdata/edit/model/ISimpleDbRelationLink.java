package de.seitenbau.govdata.edit.model;

/**
 * Interface for all classes represents a simple database type.
 * 
 * @author rnoerenberg
 *
 */
interface ISimpleDbRelationLink extends ISimpleDbRelationValue
{
  String getUrl();

  void setUrl(String url);

  boolean hasValues();
}
