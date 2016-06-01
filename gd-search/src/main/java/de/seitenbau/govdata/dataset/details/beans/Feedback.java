/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * Open Data Plaform is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with Open Data
 * Platform. If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.dataset.details.beans;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * Indicates wether userfeedback exists or not. UI can handle the state with (in JSF EL)
 * #{feedback.XXX}
 * 
 * @author Benjamin Dittwald, Fraunhofer FOKUS
 */
/**
 * @author bdi
 * 
 */
public class Feedback implements Serializable
{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The metadata app created. */
  private boolean metadataAppCreated;

  /** The metadata comment created. */
  private boolean metadataCommentCreated;

  /**
   * Checks if is metadata app created.
   * 
   * @return the metadataAppCreated
   */
  public boolean isMetadataAppCreated()
  {
    boolean tmp = metadataAppCreated;
    // invalidateBean(); // TODO ???
    return tmp;
  }

  /**
   * Sets the metadata app created.
   * 
   * @param metadataAppCreated the metadataAppCreated to set
   */
  public void setMetadataAppCreated(boolean metadataAppCreated)
  {
    this.metadataAppCreated = metadataAppCreated;
  }

  /**
   * Checks if is metadata comment created.
   * 
   * @return the metadataCommentCreated
   */
  public boolean isMetadataCommentCreated()
  {
    return metadataCommentCreated;
  }

  /**
   * Sets the metadata comment created.
   * 
   * @param metadataCommentCreated the metadataCommentCreated to set
   */
  public void setMetadataCommentCreated(boolean metadataCommentCreated)
  {
    this.metadataCommentCreated = metadataCommentCreated;
  }

}
