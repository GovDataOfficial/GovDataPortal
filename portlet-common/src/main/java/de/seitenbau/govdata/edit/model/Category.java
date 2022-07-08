package de.seitenbau.govdata.edit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category implements ISimpleDbRelationValue
{
  private static final long serialVersionUID = -8695267638224032121L;

  private Long id;

  private String name;
}
