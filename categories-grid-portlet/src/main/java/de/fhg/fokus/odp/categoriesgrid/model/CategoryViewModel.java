package de.fhg.fokus.odp.categoriesgrid.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryViewModel {

  private String name;
  
  private String title;
  
  private Integer count;
  
  private String actionURL;
}
