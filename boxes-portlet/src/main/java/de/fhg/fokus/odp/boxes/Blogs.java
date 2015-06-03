/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.fhg.fokus.odp.boxes;

// imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.service.BlogsEntryLocalServiceUtil;

/**
 * The class constitutes a bean that serves as a source for the latest blogs on
 * the start page boxes.
 * 
 * @author Majid Salehi, Fraunhofer FOKUS
 * 
 * 
 */
@ManagedBean
@SessionScoped
public class Blogs {

	/** The cache name. */
	private final String CACHE_NAME = "de.fhg.fokus.odp.boxes";

	/** The cache datasets key. */
	private final String CACHE_BLOGS_KEY = "blogs";

	/** The log. */
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	/** The maximum number of latest blogs to show. */
	private static final int maximumNumberOfBlogs = 4;

	/** The blogs. */
	private List<BlogsEntry> blogs;

	/**
	 * An init method for the bean.
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {

		blogs = (List<BlogsEntry>) MultiVMPoolUtil.get(CACHE_NAME,
				CACHE_BLOGS_KEY);

		if (blogs == null) {
			LOG.info("Empty {} cache, fetching blogs from liferay.",
					CACHE_BLOGS_KEY);
			blogs = getLatestBlogs(maximumNumberOfBlogs);
			MultiVMPoolUtil.put(CACHE_NAME, CACHE_BLOGS_KEY, blogs);
		}

	}

	private List<BlogsEntry> getLatestBlogs(int maximumnumberofblogs) {

		try {
			List<BlogsEntry> list = new ArrayList();
			blogs = new ArrayList();
			// Getting the total blogs
			int count = BlogsEntryLocalServiceUtil.getBlogsEntriesCount();
			List<BlogsEntry> blogsList = BlogsEntryLocalServiceUtil
					.getBlogsEntries(0, count);
			// Adding the non draft blogs in list
			for (BlogsEntry blogsEntry : blogsList) { 
				if (!blogsEntry.isDraft()) 
					blogs.add(blogsEntry); 																					
			}

			Collections.sort(blogs, new Comparator<BlogsEntry>() {
				public int compare(BlogsEntry a, BlogsEntry b) {
					return -(a.getCreateDate().compareTo(b.getCreateDate()));
				}
			});
			
			if (blogs.size() > maximumnumberofblogs)
				blogs = blogs.subList(0, maximumnumberofblogs);

		} catch (SystemException e) {
			e.printStackTrace();
		}

		return blogs;
	}

	/**
	 * Gets the blogs.
	 * 
	 * @return the blogs.
	 */
	public List<BlogsEntry> getBlogs() {
		return blogs;
	}

	/**
	 * Sets the blogs.
	 * 
	 * @param blogs
	 *            the blogs.
	 */
	public void setBlogs(List<BlogsEntry> blogs) {
		this.blogs = blogs;
	}

}
