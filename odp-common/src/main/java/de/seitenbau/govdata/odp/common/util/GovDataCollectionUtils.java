package de.seitenbau.govdata.odp.common.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The class provides methods for working with collections.
 * 
 * @author rnoerenberg
 */
public final class GovDataCollectionUtils
{
  private GovDataCollectionUtils()
  {
    // private
  }

  /**
   * Gets a null-safe stream from the given collection.<br>
   * Inspired from <a href= "https://www.baeldung.com/java-null-safe-streams-from-collections">
   * https://www.baeldung.com/java-null-safe-streams-from-collections</a>.
   * 
   * @param collection the collection from where we get the stream from
   * @return the stream from the collection. An empty stream if the given collection is empty or
   *         null.
   */
  public static <T> Stream<T> collectionToStream(Collection<T> collection)
  {
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }

  /**
   * Gets a null-safe stream from the given array.<br>
   * Inspired from <a href= "https://www.baeldung.com/java-null-safe-streams-from-collections">
   * https://www.baeldung.com/java-null-safe-streams-from-collections</a>.
   * 
   * @param array the collection from where we get the stream from
   * @return the stream from the array. An empty stream if the given array is empty or null.
   */
  public static <T> Stream<T> arrayToStream(T[] array)
  {
    return Optional.ofNullable(array)
        .map(Arrays::stream)
        .orElseGet(Stream::empty);
  }

  /**
   * Converts a list of strings to lower case.
   * 
   * @param toConvert list with strings to convert
   * @return the converted list
   */
  public static List<String> convertStringListToLowerCase(List<String> toConvert)
  {
    return collectionToStream(toConvert).map(String::toLowerCase).collect(Collectors.toList());
  }
}
