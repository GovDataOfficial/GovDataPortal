package de.seitenbau.govdata.edit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Type implements ISimpleDbRelationValue
{
  private static final long serialVersionUID = -2403647733632357743L;

  private Long id;

  private String name;

}
