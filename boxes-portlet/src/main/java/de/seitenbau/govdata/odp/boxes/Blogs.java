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

// imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.portal.kernel.exception.SystemException;

import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.servicetracker.BlogsEntryServiceTracker;

/**
 * The class constitutes a bean that serves as a source for the latest blogs on the start page
 * boxes.
 * 
 * @author rnoerenberg
 * 
 */
@Component
@Scope("request")
public class Blogs extends BaseBoxesBean<BlogsEntry>
{

  /** The log. */
  private static final Logger LOG = LoggerFactory.getLogger(Blogs.class);

  /** The maximum number of latest blogs to show. */
  private final int maximumNumberOfBlogs = 2;

  private BlogsEntryServiceTracker blogsEntryTracker;

  /** The blogs. */
  private List<BlogsEntry> blogs;

  /**
   * An init method for the bean.
   */
  @PostConstruct
  public void init()
  {
    // initialize BlogsEntry ServiceTracker
    initializeBlogsEntryServiceTracker();
    // read clustered cache service
    blogs = readItemsFromClusteredCache(CacheKey.BLOGS);

    if (blogs == null)
    {
      LOG.info("Empty {} cache, fetching blogs from liferay.", CacheKey.BLOGS);
      blogs = getLatestBlogs(maximumNumberOfBlogs);
      // safe cast: LinkedList
      updateCache(blogs, CacheKey.BLOGS);
    }
    LOG.debug("Initialize complete");
  }

  /**
   * Closes all open resources.
   */
  @PreDestroy
  public void close()
  {
    blogsEntryTracker.close();
  }

  private void initializeBlogsEntryServiceTracker()
  {
    if (blogsEntryTracker == null)
    {
      // The if statement is only for testing. The tracker will be injected by the mocking framework
      // before.
      blogsEntryTracker = new BlogsEntryServiceTracker(this);
    }
    blogsEntryTracker.open();
  }

  private List<BlogsEntry> getLatestBlogs(int maximumnumberofblogs)
  {
    try
    {
      blogs = new ArrayList<BlogsEntry>();
      // Getting the total blogs
      int count = blogsEntryTracker.getService().getBlogsEntriesCount();
      List<BlogsEntry> blogsList = blogsEntryTracker.getService().getBlogsEntries(0, count);
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
          // create date ascending (newest on top)
          return b.getCreateDate().compareTo(a.getCreateDate());
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
