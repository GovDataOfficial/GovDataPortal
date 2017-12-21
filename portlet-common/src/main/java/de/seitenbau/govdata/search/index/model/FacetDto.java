package de.seitenbau.govdata.search.index.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enthält einen Eintrag für eine Facette innerhalb des Suchindexes.
 * 
 * @author rnoerenberg
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacetDto
{
  private Long docCount;
  
  private String name;
}
