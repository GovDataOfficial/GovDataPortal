package de.seitenbau.govdata.common.model.exception;

/**
 * The Class UnknownShowcaseTypeException.
 * 
 * @author sgebhart
 */
public class UnknownShowcaseTypeException extends Exception
{

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new Unknown Type model exception.
   * 
   * @param message the message
   */
  public UnknownShowcaseTypeException(String message)
  {
    super(message);
  }
}
