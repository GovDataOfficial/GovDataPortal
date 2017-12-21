package de.seitenbau.govdata.odp.boxes.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import de.seitenbau.govdata.search.gui.model.HitViewModel;

/**
 * The model for displaying.
 * 
 * @author rnoerenberg
 *
 */
@Data
@NoArgsConstructor
public class DatasetModel
{
  private HitViewModel hit;
  private String licenseInfoText;
  private boolean multipleLicenses;
}
