package de.seitenbau.govdata.exception;

public class RedisNotAvailableException extends Exception
{
  /** serialVersionUID. */
  private static final long serialVersionUID = 8076383588200829891L;

  /**
   * Exception RedisNotAvailableException.
   * @param msg
   */
  public RedisNotAvailableException(String msg)
  {
    super(msg);
  }

  /**
   * Exception RedisNotAvailableException.
   * @param cause
   */
  public RedisNotAvailableException(Throwable cause)
  {
    super(cause);
  }

}
