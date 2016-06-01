package de.seitenbau.govdata.exception;

public class RedisNotAvailableException extends Exception
{
  /** serialVersionUID. */
  private static final long serialVersionUID = 8076383588200829891L;

  public RedisNotAvailableException(String msg)
  {
    super(msg);
  }

  public RedisNotAvailableException(Throwable cause)
  {
    super(cause);
  }

}
