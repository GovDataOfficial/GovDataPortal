package de.seitenbau.govdata.search.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration({
    "classpath:/spring/root-context.xml",
    // "classpath:/spring/test-gd-search-indexer-context.xml",
    // "classpath:/spring/test-gd-search-field-context.xml"
    // "classpath:/spring/test-gd-search-result-context.xml"
    "classpath:/spring/test-gd-search-details-context.xml"
})
@Ignore("Only used for manual testing, because failing if configured ckan host is unavailable!")
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringTest
{
  @Autowired
  MessageSource messageSource;

  @Test
  public void one()
  {
    String result = messageSource.getMessage("od.datasets_simple_search", new Object[] {}, Locale.GERMAN);
    /* verify */
    assertThat(result).isEqualTo("Einfache Suche");
  }
}
