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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.impl.BlogsEntryImpl;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;

import de.seitenbau.govdata.cache.BaseCache;
import de.seitenbau.govdata.servicetracker.BlogsEntryServiceTracker;
import de.seitenbau.govdata.servicetracker.MultiVMPoolServiceTracker;

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
  PortalCache<String, Serializable> portalCache;

  @Mock
  MultiVMPool multiVMPool;

  @Mock
  BlogsEntryLocalService blogsEntryLocalService;

  @Mock
  BeanLocator beanLocatorMock;

  @Mock
  MultiVMPoolServiceTracker multiVMPoolTracker;

  @Mock
  BlogsEntryServiceTracker blogsEntryTracker;

  @InjectMocks
  Blogs target;

  @Before
  public void setup() throws Exception
  {
    Mockito.when(multiVMPoolTracker.getService()).thenReturn(multiVMPool);
    Mockito.when(blogsEntryTracker.getService()).thenReturn(blogsEntryLocalService);

    // return every time an empty cache
    Mockito.when(multiVMPool.getPortalCache(BaseCache.CACHE_NAME_BOXES)).thenAnswer(
        new Answer<PortalCache<String, Serializable>>()
        {
          @Override
          public PortalCache<String, Serializable> answer(InvocationOnMock invocation) throws Throwable
          {
            return portalCache;
          }
        });
  }

  @After
  public void resetMocks()
  {
    Mockito.reset(portalCache, multiVMPool, blogsEntryLocalService, beanLocatorMock, multiVMPoolTracker);
  }

  @Test
  public void init_ListeIstNull() throws Exception
  {
    /* prepare */
    int count = 0;
    Mockito.when(blogsEntryLocalService.getBlogsEntriesCount()).thenReturn(count);
    Mockito.when(blogsEntryLocalService.getBlogsEntries(0, count)).thenReturn(null);

    /* execute */
    target.init();

    /* verify */
    assertThat(target.getBlogs()).isEmpty();
  }

  @Test
  public void init_leereListe() throws Exception
  {
    /* prepare */
    int count = 0;
    Mockito.when(blogsEntryLocalService.getBlogsEntriesCount()).thenReturn(count);
    Mockito.when(blogsEntryLocalService.getBlogsEntries(0, count)).thenReturn(new ArrayList<BlogsEntry>());

    /* execute */
    target.init();

    /* verify */
    assertThat(target.getBlogs()).isEmpty();
  }

  @Test
  public void init_ListengroesseGleichMaximumFuerAnzeige() throws Exception
  {
    /* prepare */
    int count = maximumNumberOfBlogs;
    Mockito.when(blogsEntryLocalService.getBlogsEntriesCount()).thenReturn(count);
    ArrayList<BlogsEntry> blogEntryList = new ArrayList<BlogsEntry>();
    blogEntryList.add(createBlogEntry());
    blogEntryList.add(createBlogEntry());
    Mockito.when(blogsEntryLocalService.getBlogsEntries(0, count)).thenReturn(blogEntryList);

    /* execute */
    target.init();

    /* verify */
    assertThat(target.getBlogs()).hasSize(count);
  }

  @Test
  public void init_ListengroesseGroesserMaximumFueranzeige() throws Exception
  {
    /* prepare */
    int count = maximumNumberOfBlogs + 1;
    Mockito.when(blogsEntryLocalService.getBlogsEntriesCount()).thenReturn(count);
    ArrayList<BlogsEntry> blogEntryList = new ArrayList<BlogsEntry>();
    blogEntryList.add(createBlogEntry());
    blogEntryList.add(createBlogEntry());
    blogEntryList.add(createBlogEntry());
    Mockito.when(blogsEntryLocalService.getBlogsEntries(0, count)).thenReturn(blogEntryList);

    /* execute */
    target.init();

    /* verify */
    assertThat(target.getBlogs()).hasSize(maximumNumberOfBlogs);
  }

  private BlogsEntry createBlogEntry()
  {
    BlogsEntry blogsEntryImpl = new BlogsEntryImpl();
    blogsEntryImpl.setCreateDate(new Date());
    return blogsEntryImpl;
  }
}
