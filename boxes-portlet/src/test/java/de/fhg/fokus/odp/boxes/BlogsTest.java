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

package de.fhg.fokus.odp.boxes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.lang.reflect.Field;
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

import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.model.impl.BlogsEntryImpl;
import com.liferay.portlet.blogs.service.BlogsEntryLocalService;
import com.liferay.portlet.blogs.service.BlogsEntryLocalServiceUtil;

/**
 * Tests für die Klasse {@link Blogs}.
 * 
 * @author rnoerenberg
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BlogsTest
{
  /** The cache name. */
  private final String CACHE_NAME = "de.fhg.fokus.odp.boxes";

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

  @InjectMocks
  Blogs target;

  private static MultiVMPoolUtil multiVMPoolUtil = new MultiVMPoolUtil();

  @Before
  public void setup() throws Exception
  {
    multiVMPoolUtil.setMultiVMPool(multiVMPool);
    PortalBeanLocatorUtil.setBeanLocator(beanLocatorMock);

    Mockito.when(beanLocatorMock.locate(BlogsEntryLocalService.class.getCanonicalName())).thenReturn(
        blogsEntryLocalService);

    // reset private static field "_service"
    Field field = BlogsEntryLocalServiceUtil.class.getDeclaredField("_service");
    field.setAccessible(true);
    field.set(null, null);
    field.setAccessible(false);

    // return every time an empty cache
    Mockito.when(multiVMPool.getCache(CACHE_NAME)).thenAnswer(
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
    Mockito.reset(portalCache, multiVMPool, blogsEntryLocalService, beanLocatorMock);
  }

  @Test(expected = NullPointerException.class)
  public void init_ListeIstNull() throws Exception
  {
    /* prepare */
    int count = 0;
    Mockito.when(blogsEntryLocalService.getBlogsEntriesCount()).thenReturn(count);
    Mockito.when(blogsEntryLocalService.getBlogsEntries(0, count)).thenReturn(null);

    /* execute */
    target.init();

    /* verify */
    fail("Erwarte NullPointerException!");
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

  private BlogsEntryImpl createBlogEntry()
  {
    BlogsEntryImpl blogsEntryImpl = new BlogsEntryImpl();
    blogsEntryImpl.setCreateDate(new Date());
    return blogsEntryImpl;
  }
}
