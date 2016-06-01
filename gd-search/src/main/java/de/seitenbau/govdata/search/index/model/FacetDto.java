package de.seitenbau.govdata.search.index.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacetDto
{
  private Long docCount;
  
  private String name;
}
