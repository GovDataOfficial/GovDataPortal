package de.seitenbau.govdata.edit.model;

import java.io.Serializable;

/**
 * Interface for all classes represents a simple database type.
 * 
 * @author rnoerenberg
 *
 */
interface ISimpleDbRelationValue extends Serializable
{
  Long getId();

  void setId(Long id);

  String getName();

  void setName(String name);
}
