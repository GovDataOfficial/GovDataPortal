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

package de.fhg.fokus.odp.registry.queries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

import de.fhg.fokus.odp.registry.model.MetadataEnumType;

/**
 * The Class Query.
 * 
 * @author sim
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Query implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1247046283460289410L;

    /**
     * The mode.
     */
    private QueryModeEnumType mode;

    /** The types. */
    @XmlList
    private List<MetadataEnumType> types;

    /** The searchterm. */
    @XmlElement
    private String searchterm;

    /** The categories. */
    @XmlList
    private List<String> categories;

    /** The formats. */
    @XmlList
    private List<String> formats;

    /** The licences. */
    @XmlList
    private List<String> licences;

    /** The tags. */
    private List<String> tags;

    /** The sort. */
    @XmlList
    private List<String> sort;

    /** The max. */
    @XmlElement
    private int max = 15;

    /** The offset. */
    @XmlElement
    private int offset = 0;

    /**
     * The pageoffset.
     */
    @XmlElement
    private int pageoffset = 0;

    /** The isopen. */
    @XmlElement
    private Boolean isopen;

    /**
     * The coveragefrom.
     */
    @XmlElement
    private Date coveragefrom;

    /**
     * The coverageto.
     */
    @XmlElement
    private Date coverageto;

    /**
     * Instantiates a new query.
     */
    public Query() {
        setMode(QueryModeEnumType.FILTERED);
    }

    /**
     * Instantiates a new query.
     * 
     * @param mode
     *            the mode
     */
    public Query(QueryModeEnumType mode) {
        this.setMode(mode);
    }

    /**
     * Gets the categories.
     * 
     * @return the categories
     */
    public List<String> getCategories() {
        if (categories == null) {
            categories = new ArrayList<String>();
        }
        return categories;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public List<MetadataEnumType> getTypes() {
        if (types == null) {
            types = new ArrayList<MetadataEnumType>();
        }
        return types;
    }

    /**
     * Gets the sort fields.
     * 
     * @return the sort
     */
    public List<String> getSortFields() {
        if (sort == null) {
            sort = new ArrayList<String>();
        }
        return sort;
    }

    /**
     * Gets the searchterm.
     * 
     * @return the searchterm
     */
    public String getSearchterm() {
        return searchterm;
    }

    /**
     * Sets the searchterm.
     * 
     * @param searchterm
     *            the searchterm to set
     */
    public void setSearchterm(String searchterm) {
        this.searchterm = searchterm;
    }

    /**
     * Gets the max.
     * 
     * @return the max
     */
    public int getMax() {
        return max;
    }

    /**
     * Sets the max.
     * 
     * @param max
     *            the max to set
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * Gets the offset.
     * 
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets the offset.
     * 
     * @param offset
     *            the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Gets the formats.
     * 
     * @return the formats
     */
    public List<String> getFormats() {
        if (formats == null) {
            formats = new ArrayList<String>();
        }
        return formats;
    }

    /**
     * Gets the licences.
     * 
     * @return the licences
     */
    public List<String> getLicences() {
        if (licences == null) {
            licences = new ArrayList<String>();
        }
        return licences;
    }

    /**
     * Gets the tags.
     * 
     * @return the tags
     */
    public List<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<String>();
        }
        return tags;
    }

    /**
     * Gets the checks if is open.
     * 
     * @return the checks if is open
     */
    public Boolean getIsOpen() {
        return isopen;
    }

    /**
     * Sets the checks if is open.
     * 
     * @param isopen
     *            the new checks if is open
     */
    public void setIsOpen(Boolean isopen) {
        this.isopen = isopen;
    }

    /**
     * Gets the mode.
     * 
     * @return the mode
     */
    public QueryModeEnumType getMode() {
        return mode;
    }

    /**
     * Sets the mode.
     * 
     * @param mode
     *            the mode to set
     */
    public void setMode(QueryModeEnumType mode) {
        this.mode = mode;
    }

    /**
     * Gets the pageoffset.
     * 
     * @return the pageoffset
     */
    public int getPageoffset() {
        return pageoffset;
    }

    /**
     * Sets the page offset. A page is defined as per max value. The final offset is actually calculated as (max * pageoffset) + offset.
     * 
     * @param pageoffset
     *            the pageoffset to set
     */
    public void setPageoffset(int pageoffset) {
        this.pageoffset = pageoffset;
    }

    /**
     * Gets the coverage from.
     * 
     * @return the coveragefrom
     */
    public Date getCoverageFrom() {
        return coveragefrom == null ? null : (Date) coveragefrom.clone();
    }

    /**
     * Sets the coverage from.
     * 
     * @param coveragefrom
     *            the coveragefrom to set
     */
    public void setCoverageFrom(Date coveragefrom) {
        this.coveragefrom = (Date) coveragefrom.clone();
    }

    /**
     * Gets the coverage to.
     * 
     * @return the coverageto
     */
    public Date getCoverageTo() {
        return coverageto == null ? null : (Date) coverageto.clone();
    }

    /**
     * Sets the coverage to.
     * 
     * @param coverageto
     *            the coverageto to set
     */
    public void setCoverageTo(Date coverageto) {
        this.coverageto = (Date) coverageto.clone();
    }

    /**
     * Dump.
     * 
     * @return the string
     */
    public String dump() {
        StringBuffer buffer = new StringBuffer("searchterm:");
        buffer.append(searchterm);
        return buffer.toString();
    }

}
