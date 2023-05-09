package de.seitenbau.govdata.search.filter;

import java.text.SimpleDateFormat;

import org.assertj.core.api.Assertions;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Before;
import org.junit.Test;

public class BaseFilterTest
{
  private class BaseFilterSut extends BaseFilter
  {
    public BaseFilterSut()
    {
      super("field", "fragment");
    }

    @Override
    public QueryBuilder createFilter()
    {
      return QueryBuilders.boolQuery();
    }

    @Override
    public String getLabel()
    {
      return "testLabel";
    }
  }

  private final static String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss";

  private final static String PATTERN_LABEL = "dd.MM.yyyy";

  private BaseFilter sutFilter;
  
  @Before
  public void setUp()
  {
    sutFilter = new BaseFilterSut();
  }

  @Test
  public void simpleDateFormat()
  {
    /* execute */
    SimpleDateFormat result = sutFilter.getSimpleDateFormat();

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.toPattern()).isEqualTo(PATTERN_DATE);
  }

  @Test
  public void labelDateFormat()
  {
    /* execute */
    SimpleDateFormat result = sutFilter.getLabelDateFormat();

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.toPattern()).isEqualTo(PATTERN_LABEL);
  }

  @Test
  public void simpleDateFormatNotSameObject()
  {
    /* execute */
    SimpleDateFormat result = sutFilter.getSimpleDateFormat();
    SimpleDateFormat result2 = sutFilter.getSimpleDateFormat();

    /* verify */
    Assertions.assertThat(result).isNotNull().isNotSameAs(result2);
  }

  @Test
  public void labelDateFormatNotSameObject()
  {
    /* execute */
    SimpleDateFormat result = sutFilter.getLabelDateFormat();
    SimpleDateFormat result2 = sutFilter.getLabelDateFormat();

    /* verify */
    Assertions.assertThat(result).isNotNull().isNotSameAs(result2);
  }

}
