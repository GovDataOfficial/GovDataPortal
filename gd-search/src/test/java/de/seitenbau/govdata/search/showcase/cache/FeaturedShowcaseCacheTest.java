package de.seitenbau.govdata.search.showcase.cache;

import java.util.HashSet;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.seitenbau.govdata.dataset.details.beans.SelectedShowcase;
import de.seitenbau.govdata.db.api.ShowcaseResource;
import de.seitenbau.govdata.db.api.model.Showcase;

@RunWith(MockitoJUnitRunner.class)
public class FeaturedShowcaseCacheTest
{
  @Mock
  private ShowcaseResource showcaseResource;

  @InjectMocks
  private FeaturedShowcaseCache sut;

  @Test
  public void getShowcaseForKey_KeyNull_showcaseIdZero_dbServiceNull() throws Exception
  {
    /* prepare */

    /* execute */
    SelectedShowcase result = sut.getShowcaseForKey(null, 0);

    /* assert */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void getShowcaseForKey_dbServiceNull() throws Exception
  {
    /* prepare */
    long showcaseId = 11L;
    Mockito.when(showcaseResource.read(showcaseId));

    /* execute */
    SelectedShowcase result = sut.getShowcaseForKey("k1", showcaseId);

    /* assert */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void getShowcaseForKey() throws Exception
  {
    /* prepare */
    long showcaseId = 11L;
    String title = "showcase 1";
    Showcase showcase = createShowcase(showcaseId, title, false);
    Mockito.when(showcaseResource.read(showcaseId)).thenReturn(showcase);

    /* execute */
    SelectedShowcase result = sut.getShowcaseForKey("k1", showcaseId);
    SelectedShowcase resultCached = sut.getShowcaseForKey("k1", showcaseId);

    /* assert */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getShowcase()).isNotNull();
    Assertions.assertThat(result.getShowcase().getId()).isEqualTo(showcaseId);
    Assertions.assertThat(result.getShowcase().getTitle()).isEqualTo(title);
    Assertions.assertThat(result).isSameAs(resultCached);
    Assertions.assertThat(result.getShowcase()).isSameAs(resultCached.getShowcase());
  }

  @Test
  public void getShowcaseForKey_isPrivate() throws Exception
  {
    /* prepare */
    long showcaseId = 11L;
    String title = "showcase 1";
    Showcase showcase = createShowcase(showcaseId, title, true);
    Mockito.when(showcaseResource.read(showcaseId)).thenReturn(showcase);

    /* execute */
    SelectedShowcase result = sut.getShowcaseForKey("k1", showcaseId);
    SelectedShowcase result2 = sut.getShowcaseForKey("k1", showcaseId);

    /* assert */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getShowcase()).isNotNull();
    Assertions.assertThat(result.getShowcase().getId()).isEqualTo(showcaseId);
    Assertions.assertThat(result.getShowcase().getTitle()).isEqualTo(title);
    Assertions.assertThat(result).isNotSameAs(result2);
    Assertions.assertThat(result.getShowcase()).isNotSameAs(result2.getShowcase());
  }

  @Test
  public void getShowcaseForKey_showcaseIdChanged() throws Exception
  {
    /* prepare */
    long showcaseId = 11L;
    long showcaseIdNew = 12L;
    String title = "showcase 1";
    String title2 = "showcase 2";
    Showcase showcase = createShowcase(showcaseId, title, false);
    Showcase showcase2 = createShowcase(showcaseIdNew, title2, false);
    Mockito.when(showcaseResource.read(showcaseId)).thenReturn(showcase);
    Mockito.when(showcaseResource.read(showcaseIdNew)).thenReturn(showcase2);

    /* execute */
    SelectedShowcase result = sut.getShowcaseForKey("k1", showcaseId);
    SelectedShowcase result2 = sut.getShowcaseForKey("k1", showcaseIdNew);

    /* assert */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getShowcase()).isNotNull();
    Assertions.assertThat(result.getShowcase().getId()).isEqualTo(showcaseId);
    Assertions.assertThat(result.getShowcase().getTitle()).isEqualTo(title);
    Assertions.assertThat(result).isNotSameAs(result2);
    Assertions.assertThat(result.getShowcase()).isNotSameAs(result2.getShowcase());
    Assertions.assertThat(result2.getShowcase().getId()).isEqualTo(showcaseIdNew);
    Assertions.assertThat(result2.getShowcase().getTitle()).isEqualTo(title2);
  }

  private Showcase createShowcase(long showcaseId, String title, boolean hidden)
  {
    return Showcase.builder().id(showcaseId).title(title).hidden(hidden).categories(new HashSet<>())
        .images(new HashSet<>()).keywords(new HashSet<>()).linksToShowcase(new HashSet<>())
        .platforms(new HashSet<>()).showcaseTypes(new HashSet<>()).usedDatasets(new HashSet<>()).build();
  }
}
