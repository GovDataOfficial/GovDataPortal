/**
 * Copyright (c) 2015, 2017 SEITENBAU GmbH
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * Open Data Plaform is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
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
 * The class constitutes a bean that serves as a source for the latest blogs on the start page
 * boxes.
 * 
 * @author rnoerenberg
 * 
 */
@Component
@Scope("request")
public class Blogs extends BaseBoxesBean<ViewModel>
{

  /** The log. */
  private static final Logger LOG = LoggerFactory.getLogger(Blogs.class);

  /** The maximum number of latest blogs to show. */
  private static final int MAXIMUM_NUMBER_OF_BLOGS = 2;

  /** The blogs. */
  private List<ViewModel> blogs;

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
    blogs = getLatestBlogs(MAXIMUM_NUMBER_OF_BLOGS);

    LOG.debug("Initialize complete");
  }

  private List<ViewModel> getLatestBlogs(int maximumnumberofblogs)
  {
    List<HitDto> hits = govdataResource.getLatestBlogs(maximumnumberofblogs);

    if (hits != null)
    {
      return hits.stream().map(viewModelMapper::toModel).filter(m -> Objects.nonNull(m))
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  /**
   * Gets the blogs.
   * 
   * @return the blogs.
   */
  public List<ViewModel> getBlogs()
  {
    return GovDataCollectionUtils.getCopyOfList(blogs);
  }

}
