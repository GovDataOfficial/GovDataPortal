package de.seitenbau.govdata.odp.registry.model;

import java.io.Serializable;
import java.util.List;

/**
 * The Interface Category.
 * 
 * @author rnoerenberg
 */
public interface Category extends Serializable
{

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName();

    /**
     * Gets the display name.
     * 
     * @return the display name
     */
    public String getDisplayName();

    /**
     * Gets the title.
     * 
     * @return the title
     */
    public String getTitle();

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription();

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType();

    /**
     * Gets the count.
     * 
     * @return the count
     */
    public int getCount();

    /**
     * Gets the sub categories.
     * 
     * @return the sub categories
     */
    public List<Category> getSubCategories();

    /**
     * Gets the datasets.
     * 
     * @return the datasets
     */
    public List<Metadata> getDatasets();

}
