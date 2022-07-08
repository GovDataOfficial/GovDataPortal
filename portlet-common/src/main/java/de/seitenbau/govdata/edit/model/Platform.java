package de.seitenbau.govdata.edit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Platform implements ISimpleDbRelationValue
{
  private static final long serialVersionUID = -5331932440156272999L;

  private Long id;

  private String name;
}
