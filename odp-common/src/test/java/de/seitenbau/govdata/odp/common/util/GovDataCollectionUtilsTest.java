package de.seitenbau.govdata.odp.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Test;

public class GovDataCollectionUtilsTest
{
  @Test
  public void collectionToStream_null()
  {
    /* execute */
    Stream<?> result = GovDataCollectionUtils.collectionToStream(null);

    /* assert */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.count()).isZero();
  }

  @Test
  public void collectionToStream_empty()
  {
    /* execute */
    Stream<?> result = GovDataCollectionUtils.collectionToStream(new ArrayList<>());

    /* assert */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.count()).isZero();
  }

  @Test
  public void collectionToStream()
  {
    /* prepare */
    List<String> list = Lists.newArrayList("one", "two");

    /* execute */
    Stream<String> result = GovDataCollectionUtils.collectionToStream(list);

    /* assert */
    List<String> actual = result.collect(Collectors.toList());
    Assertions.assertThat(actual).containsExactly(list.toArray(new String[0]));
  }

  @Test
  public void convertStringListToLowerCase_null()
  {
    /* execute */
    List<String> result = GovDataCollectionUtils.convertStringListToLowerCase(null);

    /* assert */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void convertStringListToLowerCase_empty()
  {
    /* execute */
    List<?> result = GovDataCollectionUtils.convertStringListToLowerCase(new ArrayList<>());

    /* assert */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void convertStringListToLowerCase()
  {
    /* prepare */
    List<String> list = Lists.newArrayList("One", "TWO");

    /* execute */
    List<String> result = GovDataCollectionUtils.convertStringListToLowerCase(list);

    /* assert */
    Assertions.assertThat(result).containsExactly("one", "two");
  }
}
