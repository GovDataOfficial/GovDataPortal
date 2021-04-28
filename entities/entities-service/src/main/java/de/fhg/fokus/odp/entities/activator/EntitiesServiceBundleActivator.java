package de.fhg.fokus.odp.entities.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.upgrade.release.BaseUpgradeServiceModuleRelease;

public class EntitiesServiceBundleActivator implements BundleActivator
{
  private ServiceTracker<Object, Object> _serviceTracker;

  @Override
  public void start(BundleContext context) throws Exception
  {
    Filter filter = context.createFilter(
        StringBundler.concat(
            "(&(objectClass=", ModuleServiceLifecycle.class.getName(), ")",
            ModuleServiceLifecycle.DATABASE_INITIALIZED, ")"));

    _serviceTracker = new ServiceTracker<Object, Object>(
        context, filter, null)
    {
      @Override
      public Object addingService(
          ServiceReference<Object> serviceReference)
      {

        try
        {
          BaseUpgradeServiceModuleRelease upgradeServiceModuleRelease =
              new BaseUpgradeServiceModuleRelease()
              {

                @Override
                protected String getNamespace()
                {
                  return "entities";
                }

                @Override
                protected String getNewBundleSymbolicName()
                {
                  return "de.fhg.fokus.odp.entities.service";
                }

                @Override
                protected String getOldBundleSymbolicName()
                {
                  return "entities_WAR_entitiesportlet";
                }

              };

          upgradeServiceModuleRelease.upgrade();

          return null;
        }
        catch (UpgradeException ue)
        {
          throw new RuntimeException(ue);
        }
      }
    };

    _serviceTracker.open();
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    _serviceTracker.close();
  }

}
