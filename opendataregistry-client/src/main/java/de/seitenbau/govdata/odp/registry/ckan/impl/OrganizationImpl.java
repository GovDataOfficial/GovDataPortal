package de.seitenbau.govdata.odp.registry.ckan.impl;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

import de.seitenbau.govdata.odp.registry.ckan.json.OrganizationBean;
import de.seitenbau.govdata.odp.registry.model.Organization;

public class OrganizationImpl implements Organization, Serializable
{
  private static final long serialVersionUID = -4316531700498190834L;
  private OrganizationBean bean;

  public OrganizationImpl(OrganizationBean bean)
  {
    this.bean = bean;
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
  public String toString()
  {
    return "Organization{id: " + getId() + ", name: " + getName() + ", displayName: " + getDisplayName() + ", title: " + getTitle();
  }

  @Override
  public int compareTo(Organization o)
  {
    
    Collator collator = Collator.getInstance(Locale.GERMAN);
    collator.setStrength(Collator.SECONDARY);// a == A, a < Ä
    return collator.compare(this.getTitle(), o.getTitle());
  }
}
