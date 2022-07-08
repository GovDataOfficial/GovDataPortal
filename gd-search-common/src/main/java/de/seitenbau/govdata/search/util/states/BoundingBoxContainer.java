package de.seitenbau.govdata.search.util.states;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoundingBoxContainer
{
  private double latitudeMax;

  private double latitudeMin;

  private double longitudeMax;

  private double longitudeMin;
}