package de.seitenbau.govdata.odp.registry.model;

/**
 * The Interface Tag.
 * 
 * @author sim
 */
public interface Tag
{

    /**
     * Gets the name. The name is the tag itself.
     * 
     * @return the name
     */
    public String getName();

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription();

    /**
     * Gets the count. The number of metadata tagged with this.
     * 
     * @return the count
     */
    public long getCount();

}
