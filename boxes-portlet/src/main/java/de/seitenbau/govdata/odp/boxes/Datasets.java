/**
 * Copyright (c) 2015, 2017 SEITENBAU GmbH
 * <p>
 * This file is part of Open Data Platform.
 * <p>
 * Open Data Platform is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * <p>
 * Open Data Plaform is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License along with Open Data
 * Platform. If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.odp.boxes;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.seitenbau.govdata.data.api.GovdataResource;
import de.seitenbau.govdata.odp.boxes.mapper.ViewModelMapper;
import de.seitenbau.govdata.odp.boxes.model.ViewModel;
import de.seitenbau.govdata.odp.common.util.GovDataCollectionUtils;
import de.seitenbau.govdata.search.api.model.search.dto.HitDto;

/**
 * The class constitutes a bean that serves as a source for the latest datasets on the start page
 * boxes.
 *
 * @author rnoerenberg
 *
 */
@Component
@Scope("request")
public class Datasets extends BaseBoxesBean<ViewModel>
{
  /** The log. */
  private static final Logger LOG = LoggerFactory.getLogger(Datasets.class);

  /** The maximum number of the latest datasets to show. */
  private static final int MAXIMUM_NUMBER_OF_DATASETS = 4;

  /** The datasets. */
  private List<ViewModel> datasets;

  @Inject
  private GovdataResource govdataResource;

  @Inject
  private ViewModelMapper viewModelMapper;

  /**
   * An init method for the bean.
   */
  @PostConstruct
  public void init()
  {
    datasets = getLatestDatasets(MAXIMUM_NUMBER_OF_DATASETS);

    LOG.debug("Initialize complete");
  }

  private List<ViewModel> getLatestDatasets(int maximumNumberOfDatasets)
  {
    List<HitDto> hits = govdataResource.getLatestDatasets(maximumNumberOfDatasets);
    if (hits != null)
    {
      return hits.stream().map(viewModelMapper::toModel).filter(m -> Objects.nonNull(m))
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  /**
   * Gibt die maximale Anzahl an Einträgen für die Anzeige in der ersten Spalte zurück.
   * 
   * @return die maximale Anzahl an Einträgen für die erste Spalte.
   */
  public int getFirstColumnLength()
  {
    List<ViewModel> datasets = this.getDatasets();
    return Math.min(datasets.size(), 2);
  }

  /**
   * Gets the datasets.
   * 
   * @return the datasets.
   */
  public List<ViewModel> getDatasets()
  {
    return GovDataCollectionUtils.getCopyOfList(datasets);
  }
}
