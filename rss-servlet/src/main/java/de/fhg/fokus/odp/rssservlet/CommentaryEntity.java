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

package de.fhg.fokus.odp.rssservlet;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

/**
 * The Class CommentaryEntity.
 */
@NamedNativeQueries({ @NamedNativeQuery(name = "getAllCommentsOrderedByCreatedDesc", query = "SELECT entities_metadatacomment.metadataname, entities_metadatacomment.text_, entities_metadatacomment.created, entities_metadatacomment._id, user_.screenname FROM entities_metadatacomment, user_ WHERE entities_metadatacomment.userliferayid = user_.userid ORDER BY entities_metadatacomment.created DESC;", resultClass = CommentaryEntity.class) })
@Entity
@Table(name = "entities_metadatacomment")
public class CommentaryEntity {

    /** The id. */
    @Id
    @Column(name = "_id")
    private long id;

    /** The text. */
    @Column(name = "text_")
    private String text;

    /** The created. */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    /** The metadataname. */
    @Column
    private String metadataname;

    /** The liferay screen name. */
    @Column(name = "screenname")
    private String liferayScreenName;

    /**
     * Gets the text.
     * 
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text.
     * 
     * @param text
     *            the new text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the created.
     * 
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Sets the created.
     * 
     * @param created
     *            the new created
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * Gets the metadataname.
     * 
     * @return the metadataname
     */
    public String getMetadataname() {
        return metadataname;
    }

    /**
     * Sets the metadataname.
     * 
     * @param metadataname
     *            the new metadataname
     */
    public void setMetadataname(String metadataname) {
        this.metadataname = metadataname;
    }

    /**
     * Gets the liferay screen name.
     * 
     * @return the liferay screen name
     */
    public String getLiferayScreenName() {
        return liferayScreenName;
    }

    /**
     * Sets the liferay screen name.
     * 
     * @param liferayScreenName
     *            the new liferay screen name
     */
    public void setLiferayScreenName(String liferayScreenName) {
        this.liferayScreenName = liferayScreenName;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the new id
     */
    public void setId(long id) {
        this.id = id;
    }

}
