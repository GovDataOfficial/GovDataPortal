package de.seitenbau.govdata.edit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Keyword implements ISimpleDbRelationValue
{
  private static final long serialVersionUID = 8312784501452340004L;

  private Long id;

  private String name;
}
