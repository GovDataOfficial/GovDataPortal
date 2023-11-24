package de.seitenbau.govdata.odp.registry.ckan.impl;

import java.io.Serializable;
import java.util.List;

import de.seitenbau.govdata.odp.registry.ckan.json.GroupBean;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.Metadata;

/**
 * Die Implementierung für das Interface {@link Category}.
 * 
 * @author sim
 * @author rnoerenberg
 * 
 */
public class CategoryImpl implements Category, Serializable
{

  /**
     * 
     */
  private static final long serialVersionUID = 2655069689597975300L;

  private GroupBean group;

  /**
   * Konstruktor mit {@link GroupBean}.
   * 
   * @param group die {@link GroeupBean}.
   */
  public CategoryImpl(GroupBean group)
  {
    this.group = group;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.Category#getName()
   */
  @Override
  public String getName()
  {
    return group.getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.Category#getDisplayName()
   */
  @Override
  public String getDisplayName()
  {
    return group.getDisplayName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.Category#getTitle()
   */
  @Override
  public String getTitle()
  {
    return group.getTitle();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.Category#getDescription()
   */
  @Override
  public String getDescription()
  {
    return group.getDescription();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.Category#getType()
   */
  @Override
  public String getType()
  {
    return group.getType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.Category#getDatasets()
   */
  @Override
  public List<Metadata> getDatasets()
  {
    return null;
  }

  @Override
  public int getCount()
  {
    return Integer.parseInt(group.getPackageCount());
  }

  @Override
  public List<Category> getSubCategories()
  {
    return null;
  }

}
