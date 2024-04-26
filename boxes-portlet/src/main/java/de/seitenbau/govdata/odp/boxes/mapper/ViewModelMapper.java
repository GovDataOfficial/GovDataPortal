package de.seitenbau.govdata.odp.boxes.mapper;

import java.util.Optional;

import javax.inject.Inject;

import de.seitenbau.govdata.odp.boxes.model.ViewModel;
import de.seitenbau.govdata.search.api.model.search.dto.HitDto;
import de.seitenbau.govdata.search.gui.mapper.SearchResultsViewMapper;
import de.seitenbau.govdata.search.gui.model.HitViewModel;
import de.seitenbau.govdata.search.gui.model.LicenseViewModel;

/**
 * Mappt zwischen {@link HitDto} und {@link ViewModel}.
 *
 * @author youalad
 *
 */
public class ViewModelMapper
{
  @Inject
  private SearchResultsViewMapper searchResultsMapper;

  /**
   * Mappt ein {@link HitDto} zu einem {@link ViewModel}.
   *
   * @param hit
   * @return
   */
  public ViewModel toModel(HitDto hit)
  {
    HitViewModel hitViewModel;
    try
    {
      hitViewModel = searchResultsMapper.mapHitDtoToHitsViewModel(hit, null, null, false);
    }
    catch (Exception e)
    {
      return null;
    }

    ViewModel datasetModel = new ViewModel();
    datasetModel.setHit(hitViewModel);

    if (hitViewModel.getResourcesLicenses().size() > 1)
    {
      datasetModel.setMultipleLicenses(true);
    }
    else
    {
      Optional<LicenseViewModel> licenceOptional = hitViewModel.getResourcesLicenses().stream().findFirst();
      if (licenceOptional.isPresent())
      {
        datasetModel.setLicenseInfoText(licenceOptional.get().getTitle());
      }
    }

    return datasetModel;
  }
}
