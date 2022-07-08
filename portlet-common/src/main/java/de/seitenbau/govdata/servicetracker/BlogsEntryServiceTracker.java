package de.seitenbau.govdata.servicetracker;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.blogs.service.BlogsEntryLocalService;

public class BlogsEntryServiceTracker extends ServiceTracker<BlogsEntryLocalService, BlogsEntryLocalService>
{

  /**
   * Constructor with host.
   * 
   * @param host
   */
  public BlogsEntryServiceTracker(Object host)
  {
    super(
        FrameworkUtil.getBundle(host.getClass()).getBundleContext(),
        BlogsEntryLocalService.class, null);
  }

}
