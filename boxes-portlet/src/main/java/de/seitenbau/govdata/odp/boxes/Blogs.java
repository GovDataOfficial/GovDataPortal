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

import java.io.Serializable;
// imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.service.BlogsEntryLocalServiceUtil;

import de.seitenbau.govdata.cache.BaseCache;
import de.seitenbau.govdata.clean.StringCleaner;

/**
 * The class constitutes a bean that serves as a source for the latest blogs on the start page
 * boxes.
 * 
 * @author rnoerenberg
 * 
 */
@Component
@Scope("request")
public class Blogs implements Serializable
{
  private static final long serialVersionUID = 5336951701667722369L;

  /** The cache datasets key. */
  private final String CACHE_BLOGS_KEY = "blogs";

  /** The log. */
  private static final Logger LOG = LoggerFactory.getLogger(Blogs.class);

  /** The maximum number of latest blogs to show. */
  private final int maximumNumberOfBlogs = 2;

  /** The blogs. */
  private List<BlogsEntry> blogs;

  /**
   * An init method for the bean.
   */
  @SuppressWarnings("unchecked")
  @PostConstruct
  public void init()
  {

    blogs = (List<BlogsEntry>) MultiVMPoolUtil.getCache(BaseCache.CACHE_NAME_BOXES).get(CACHE_BLOGS_KEY);

    if (blogs == null)
    {
      LOG.info("Empty {} cache, fetching blogs from liferay.",
          CACHE_BLOGS_KEY);
      blogs = getLatestBlogs(maximumNumberOfBlogs);
      // safe cast: LinkedList
      MultiVMPoolUtil.getCache(BaseCache.CACHE_NAME_BOXES).put(CACHE_BLOGS_KEY, (Serializable) blogs);
    }

  }

  private List<BlogsEntry> getLatestBlogs(int maximumnumberofblogs)
  {
    try
    {
      blogs = new ArrayList<BlogsEntry>();
      // Getting the total blogs
      int count = BlogsEntryLocalServiceUtil.getBlogsEntriesCount();
      List<BlogsEntry> blogsList = BlogsEntryLocalServiceUtil.getBlogsEntries(0, count);
      // Adding the non draft blogs in list
      Optional.ofNullable(blogsList)
          .orElseGet(Collections::emptyList)
          .stream()
          .filter(b -> Objects.nonNull(b))
          .filter(b -> (!b.isDraft() && !b.isInTrash()))
          .forEach(b ->
            {
              // strip content of html-tags
              String content = b.getContent();
              b.setContent(StringCleaner.trimAndFilterString(content));
              blogs.add(b);
            });

      Collections.sort(blogs, new Comparator<BlogsEntry>()
      {
        @Override
        public int compare(BlogsEntry a, BlogsEntry b)
        {
          return -(a.getCreateDate().compareTo(b.getCreateDate()));
        }
      });

      if (blogs.size() > maximumnumberofblogs)
      {
        blogs = new ArrayList<BlogsEntry>(blogs.subList(0, maximumnumberofblogs));
      }
    }
    catch (SystemException e)
    {
      LOG.error("Fehler beim Lesen der Blogeinträge.", e);
    }

    return blogs;
  }

  /**
   * Gets the blogs.
   * 
   * @return the blogs.
   */
  public List<BlogsEntry> getBlogs()
  {
    return blogs;
  }

  /**
   * Sets the blogs.
   * 
   * @param blogs the blogs.
   */
  public void setBlogs(List<BlogsEntry> blogs)
  {
    this.blogs = blogs;
  }

}
