package de.seitenbau.govdata.odp.registry.ckan.impl;

import java.io.Serializable;
import java.util.Objects;

import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.LicenceConformance;

/**
 * The Class LicenceImpl.
 *
 * @author sim
 */
public class LicenceImpl implements Licence, Serializable
{
  private static final long serialVersionUID = -6730255931161315851L;

  private final LicenceBean licence;

  /**
   * Constructor with license bean.
   * 
   * @param licence the licence bean,
   */
  public LicenceImpl(LicenceBean licence)
  {
    this.licence = licence;
  }

  @Override
  public String getTitle()
  {
    return licence.getTitle();
  }

  @Override
  public String getId()
  {
    return licence.getId();
  }

  @Override
  public String getUrl()
  {
    return licence.getUrl();
  }

  @Override
  public String getOdConformance()
  {
    return licence.getOd_conformance();
  }

  @Override
  public String getOsdConformance()
  {
    return licence.getOsd_conformance();
  }

  @Override
  public boolean isActive()
  {
    return Objects.equals(licence.getStatus(), "active");
  }

  @Override
  public boolean isOpen()
  {
    return (getOdConformance() != null && getOdConformance().equals(LicenceConformance.APPROVED.getValue()))
        || (getOsdConformance() != null
            && getOsdConformance().equals(LicenceConformance.APPROVED.getValue()));
  }
}
