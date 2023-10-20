package de.fhg.fokus.odp.entities.registry;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.release.ReleaseRenamingUpgradeStep;

@Component(service = UpgradeStepRegistrator.class)
public class EntitiesServiceUpgrade implements UpgradeStepRegistrator
{

  @Override
  public void register(Registry registry)
  {
    registry.registerReleaseCreationUpgradeSteps(
        new ReleaseRenamingUpgradeStep(
            "de.fhg.fokus.odp.entities.service", "entities_WAR_entitiesportlet",
            _releaseLocalService));

    registry.register("0.0.1", "1.0.0", new DummyUpgradeStep());
  }

  @Reference
  private ReleaseLocalService _releaseLocalService;

}
