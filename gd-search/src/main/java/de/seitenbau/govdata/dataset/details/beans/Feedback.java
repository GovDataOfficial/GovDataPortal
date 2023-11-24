package de.seitenbau.govdata.dataset.details.beans;

import java.io.Serializable;

/**
 * Indicates wether userfeedback exists or not. UI can handle the state with (in JSF EL)
 * #{feedback.XXX}.
 * 
 * @author Benjamin Dittwald, Fraunhofer FOKUS
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
