package de.seitenbau.govdata.servicetracker;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.portal.kernel.cache.MultiVMPool;

public class MultiVMPoolServiceTracker extends ServiceTracker<MultiVMPool, MultiVMPool>
{

  /**
   * Constructor.
   * @param host
   */
  public MultiVMPoolServiceTracker(Object host)
  {
    super(
        FrameworkUtil.getBundle(host.getClass()).getBundleContext(),
        MultiVMPool.class, null);
  }

}
