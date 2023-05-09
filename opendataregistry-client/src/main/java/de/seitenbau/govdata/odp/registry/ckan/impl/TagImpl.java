/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */
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
