package de.seitenbau.govdata.developerscorner.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SparqlEndpoint
{
  private String type;

  private String name;

  private String url;
}
