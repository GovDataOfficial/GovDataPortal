/**
 * Copyright (c) 2015 SEITENBAU GmbH
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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.seitenbau.govdata.data.api.GovdataResource;
import de.seitenbau.govdata.odp.boxes.mapper.ViewModelMapper;
import de.seitenbau.govdata.odp.boxes.model.ViewModel;
import de.seitenbau.govdata.search.api.model.search.dto.HitDto;
import de.seitenbau.govdata.search.gui.model.HitViewModel;

/**
 * Tests für die Klasse {@link Blogs}.
 * 
 * @author rnoerenberg
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BlogsTest
{
  /** The maximum number of latest blogs to show. */
  private static final int maximumNumberOfBlogs = 2;

  @Mock
  ViewModelMapper viewModelMapper;

  @Mock
  GovdataResource govdataResource;

  @InjectMocks
  Blogs target;

  @Test
  public void init_ListeIstNull() throws Exception
  {
    /* prepare */
    Mockito.when(govdataResource.getLatestBlogs(maximumNumberOfBlogs)).thenReturn(null);

    /* execute */
    target.init();

    /* verify */
    Mockito.verify(viewModelMapper, Mockito.times(0)).toModel(Mockito.any());
    assertThat(target.getBlogs()).isEmpty();
  }

  @Test
  public void init() throws Exception
  {
    /* prepare */
    List<HitDto> blogEntryList = new ArrayList<HitDto>();
    blogEntryList.add(createBlogEntry());
    blogEntryList.add(createBlogEntry());
    Mockito.when(govdataResource.getLatestBlogs(maximumNumberOfBlogs)).thenReturn(blogEntryList);

    for (HitDto hit : blogEntryList)
    {
      Mockito.when(viewModelMapper.toModel(hit)).thenReturn(createViewModelFromHitDto(hit));
    }

    /* execute */
    target.init();

    /* verify */
    Mockito.verify(viewModelMapper, Mockito.times(2)).toModel(Mockito.any());
    for (HitDto hit : blogEntryList)
    {
      Mockito.verify(viewModelMapper, Mockito.times(1)).toModel(hit);
    }
    assertThat(target.getBlogs()).hasSize(maximumNumberOfBlogs);
  }

  private HitDto createBlogEntry()
  {
    return createBlogEntry(RandomStringUtils.randomAlphanumeric(8), Instant.now());
  }

  private HitDto createBlogEntry(String id, Instant instant)
  {
    HitDto hitDto = new HitDto();
    hitDto.setId(id);
    hitDto.setReleaseDate(Date.from(instant));
    hitDto.setType("test type");
    hitDto.setName("test name");
    return hitDto;
  }

  private ViewModel createViewModelFromHitDto(HitDto hit)
  {
    HitViewModel hitViewModel = new HitViewModel();
    hit.setId(hit.getId());
    hit.setReleaseDate(hit.getReleaseDate());
    ViewModel viewModel = new ViewModel();
    viewModel.setHit(hitViewModel);
    return viewModel;
  }
}
