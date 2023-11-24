package de.seitenbau.govdata.odp.registry.ckan.impl;

import java.io.Serializable;

import de.seitenbau.govdata.odp.registry.ckan.json.TagBean;
import de.seitenbau.govdata.odp.registry.model.Tag;

/**
 * Implementation for the interface {@link Tag}.
 * 
 */
public class TagImpl implements Tag, Serializable
{
    private static final long serialVersionUID = -2353397328621277220L;

    private TagBean tag;

    private long count;

    /**
     * Constructor with tag bean.
     * 
     * @param tag
     */
    public TagImpl(TagBean tag)
    {
        this.tag = tag;
    }

    @Override
    public String getName()
    {
        return tag.getName();
    }

    @Override
    public long getCount()
    {
        return count;
    }

    public void setCount(long count)
    {
        this.count = count;
    }

    @Override
    public String getDescription()
    {
        return tag.getDisplay_name();
    }

    public TagBean getBean()
    {
        return tag;
    }

}
