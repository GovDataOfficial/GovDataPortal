package de.seitenbau.govdata.odp.registry.ckan.impl;

import java.io.Serializable;
import java.text.Collator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.seitenbau.govdata.odp.registry.ckan.Util;
import de.seitenbau.govdata.odp.registry.ckan.json.ExtraBean;
import de.seitenbau.govdata.odp.registry.ckan.json.OrganizationBean;
import de.seitenbau.govdata.odp.registry.model.MetadataListExtraFields;
import de.seitenbau.govdata.odp.registry.model.Organization;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 *
 */
public class OrganizationImpl implements Organization, Serializable
{
  private static final long serialVersionUID = -4316531700498190834L;
  private OrganizationBean bean;

  private Map<String, ExtraBean> extras = new HashMap<>();

  /**
   * Constructor with bean.
   * 
   * @param bean
   */
  public OrganizationImpl(OrganizationBean bean)
  {
    this.bean = bean;
    for (ExtraBean extraBean : bean.getExtras())
    {
      extras.put(extraBean.getKey(), extraBean);
    }
  }

  @Override
  public String getId()
  {
    return bean.getId();
  }

  @Override
  public String getName()
  {
    return bean.getName();
  }

  @Override
  public String getDisplayName()
  {
    return bean.getDisplay_name();
  }

  @Override
  public String getTitle()
  {
    return bean.getTitle();
  }
  
  @Override
  public List<String> getContributorIds()
  {
    ExtraBean extraBean = extras.get(MetadataListExtraFields.CONTRIBUTOR_ID.getField());
    if (extraBean == null)
    {
      return Collections.emptyList();
    }
    return Util.readJsonList(extraBean.getValue());
  }

  @Override
  public String toString()
  {
    return "Organization{id: " + getId() + ", name: " + getName() + ", displayName: " + getDisplayName()
        + ", title: " + getTitle() + ", contributorIds: " + getContributorIds();
  }

  @Override
  public int compareTo(Organization o)
  {
    
    Collator collator = Collator.getInstance(Locale.GERMAN);
    collator.setStrength(Collator.SECONDARY); // a == A, a < Ä
    return collator.compare(this.getTitle(), o.getTitle());
  }
}
