package de.seitenbau.govdata.search.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class QuerySanatizerTest
{
  @Test
  public void sanatizeQuery_null() throws Exception
  {

    /* prepare */

    /* execute */
    String result = QuerySanatizer.sanatizeQuery(null);

    /* assert */
    assertThat(result).isNull();
  }

  @Test
  public void sanatizeQuery_empty() throws Exception
  {

    /* prepare */

    /* execute */
    String result = QuerySanatizer.sanatizeQuery("");

    /* assert */
    assertThat(result).isEmpty();
  }

  @Test
  public void sanatizeQuery_blank() throws Exception
  {

    /* prepare */

    /* execute */
    String result = QuerySanatizer.sanatizeQuery("  ");

    /* assert */
    assertThat(result).isEmpty();
  }

  @Test
  public void sanatizeQuery() throws Exception
  {

    /* prepare */

    /* execute */
    String result = QuerySanatizer.sanatizeQuery("f[fg]<b>sd</b>/f<div>dgg</div>d/");

    /* assert */
    assertThat(result).isEqualTo("f[fg]sd f dgg d ");
  }
}
