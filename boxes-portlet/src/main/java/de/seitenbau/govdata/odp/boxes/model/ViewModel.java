package de.seitenbau.govdata.odp.boxes.model;

import de.seitenbau.govdata.search.gui.model.HitViewModel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The model for displaying.
 * 
 * @author rnoerenberg
 *
 */
@Data
@NoArgsConstructor
public class ViewModel
{
  private HitViewModel hit;
  private String licenseInfoText;
  private boolean multipleLicenses;
}
