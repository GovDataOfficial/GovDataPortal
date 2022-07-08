package de.seitenbau.govdata.dataset.details.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.liferay.portal.kernel.model.User;

import de.seitenbau.govdata.common.showcase.model.ShowcaseTypeEnum;
import de.seitenbau.govdata.edit.model.Keyword;
import de.seitenbau.govdata.edit.model.ShowcaseViewModel;
import de.seitenbau.govdata.edit.model.Type;
import de.seitenbau.govdata.odp.registry.ckan.impl.CategoryImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.GroupBean;
import de.seitenbau.govdata.odp.registry.model.Category;

@RunWith(MockitoJUnitRunner.class)
public class SelectedShowcaseTest
{
  private SelectedShowcase sut;

  private ShowcaseViewModel showcaseViewModel;

  private CurrentUser currentUser;

  @Mock
  private User liferayUser;

  private Map<String, Category> categoryMap;

  @Before
  public void setup()
  {
    showcaseViewModel = new ShowcaseViewModel();
    currentUser = new CurrentUser();
    currentUser.setLiferayUser(liferayUser);
    categoryMap = new HashMap<>();
    sut = new SelectedShowcase(showcaseViewModel, currentUser, categoryMap);
  }

  @Test
  public void getTitleOnlyText_title_null() throws Exception
  {
    /* prepare */

    /* execute */
    String result = sut.getTitleOnlyText();

    /* assert */
    assertThat(result).isEmpty();
    assertThat(sut.getShowcase().getTitle()).isNull();
  }

  @Test
  public void getTitleOnlyText_title() throws Exception
  {
    /* prepare */
    sut.getShowcase().setTitle("<b>text</b> <script>script content</script> and text");

    /* execute */
    String result = sut.getTitleOnlyText();

    /* assert */
    assertThat(result).isEqualTo("text and text");
  }

  @Test
  public void getNotesOnlyText_notes_null() throws Exception
  {
    /* prepare */

    /* execute */
    String result = sut.getNotesOnlyText();

    /* assert */
    assertThat(result).isEmpty();
    assertThat(sut.getShowcase().getNotes()).isNull();
  }

  @Test
  public void getNotesOnlyText_notes() throws Exception
  {
    /* prepare */
    sut.getShowcase().setNotes("<b>text</b> <script>script content</script> and text");

    /* execute */
    String result = sut.getNotesOnlyText();

    /* assert */
    assertThat(result).isEqualTo("text and text");
  }

  @Test
  public void getNotesValidated_notes_null() throws Exception
  {
    /* prepare */

    /* execute */
    String result = sut.getNotesValidated();

    /* assert */
    assertThat(result).isEmpty();
    assertThat(sut.getShowcase().getNotes()).isNull();
  }

  @Test
  public void getNotesValidated_notes() throws Exception
  {
    /* prepare */
    sut.getShowcase().setNotes("<b>text</b> <script>script content</script> and text");

    /* execute */
    String result = sut.getNotesValidated();

    /* assert */
    assertThat(result).isEqualTo("<b>text</b>  and text");
  }

  @Test
  public void isBlockelementInNotes_notes_null() throws Exception
  {
    /* prepare */

    /* execute */
    boolean result = sut.isBlockelementInNotes();

    /* assert */
    assertThat(result).isFalse();
    assertThat(sut.getShowcase().getNotes()).isNull();
  }

  @Test
  public void isBlockelementInNotes_no() throws Exception
  {
    /* prepare */
    sut.getShowcase().setNotes("<b>text</b> <script>script content</script> and text");

    /* execute */
    boolean result = sut.isBlockelementInNotes();

    /* assert */
    assertThat(result).isFalse();
  }

  @Test
  public void isBlockelementInNotes_yes_ptag() throws Exception
  {
    /* prepare */
    sut.getShowcase().setNotes("<b>text</b> <script>script content</script> and <p>text</p>");

    /* execute */
    boolean result = sut.isBlockelementInNotes();

    /* assert */
    assertThat(result).isTrue();
  }

  @Test
  public void isBlockelementInNotes_yes_litag() throws Exception
  {
    /* prepare */
    sut.getShowcase().setNotes("<b>text</b> <script>script content</script> and <li>text</li>");

    /* execute */
    boolean result = sut.isBlockelementInNotes();

    /* assert */
    assertThat(result).isTrue();
  }

  @Test
  public void isBlockelementInNotes_yes_oltag() throws Exception
  {
    /* prepare */
    sut.getShowcase().setNotes("<b>text</b> <script>script content</script> and <ol>text</ol>");

    /* execute */
    boolean result = sut.isBlockelementInNotes();

    /* assert */
    assertThat(result).isTrue();
  }

  @Test
  public void isBlockelementInNotes_yes_ultag() throws Exception
  {
    /* prepare */
    sut.getShowcase().setNotes("<b>text</b> <script>script content</script> and <ul>text</ul>");

    /* execute */
    boolean result = sut.isBlockelementInNotes();

    /* assert */
    assertThat(result).isTrue();
  }

  @Test
  public void getShowcaseTypeDisplayName_null() throws Exception
  {
    /* prepare */

    /* execute */
    String result = sut.getShowcaseTypeDisplayName();

    /* assert */
    assertThat(result).isEmpty();
    assertThat(sut.getShowcase().getPrimaryShowcaseType()).isNull();
    assertThat(sut.getShowcase().getAdditionalShowcaseTypes()).isNull();
  }

  @Test
  public void getShowcaseTypeDisplayName_unknown() throws Exception
  {
    /* prepare */
    String primaryShowcaseName = "unknown";
    String additionalShowcaseName = "alsoUnknown";

    sut.getShowcase().setPrimaryShowcaseType(new Type(1L, primaryShowcaseName));
    List<Type> additionalShowcase = new ArrayList<>();
    additionalShowcase.add(new Type(2L, additionalShowcaseName));
    sut.getShowcase().setAdditionalShowcaseTypes(additionalShowcase);

    /* execute */
    String result = sut.getShowcaseTypeDisplayName();

    /* assert */
    String expectedResult = primaryShowcaseName + ", " + additionalShowcaseName;
    assertThat(result).isEqualTo(expectedResult);
  }

  @Test
  public void getShowcaseTypeDisplayName() throws Exception
  {
    /* prepare */
    String primaryShowcaseName = ShowcaseTypeEnum.APP.getField();
    String additionalShowcaseName1 = ShowcaseTypeEnum.CONCEPT.getField();
    String additionalShowcaseName2 = ShowcaseTypeEnum.TOOL.getField();

    sut.getShowcase().setPrimaryShowcaseType(new Type(1L, primaryShowcaseName));
    List<Type> additionalShowcases = new ArrayList<>();
    additionalShowcases.add(new Type(2L, additionalShowcaseName1));
    additionalShowcases.add(new Type(3L, additionalShowcaseName2));
    sut.getShowcase().setAdditionalShowcaseTypes(additionalShowcases);

    /* execute */
    String result = sut.getShowcaseTypeDisplayName();

    /* assert */
    String expectedResult = ShowcaseTypeEnum.APP.getDisplayName() + ", "
        + ShowcaseTypeEnum.CONCEPT.getDisplayName() + ", " + ShowcaseTypeEnum.TOOL.getDisplayName();
    assertThat(result).isEqualTo(expectedResult);
  }

  @Test
  public void hasLinksToDatasets_null() throws Exception
  {
    /* prepare */

    /* execute */
    boolean result = sut.hasLinksToDatasets();

    /* assert */
    assertThat(result).isFalse();
  }

  @Test
  public void hasLinksToDatasets() throws Exception
  {
    /* prepare */
    sut.getShowcase().addEmptyLinkToUsedDataset();

    /* execute */
    boolean result = sut.hasLinksToDatasets();

    /* assert */
    assertThat(result).isTrue();
  }

  @Test
  public void hasLinksToShowcase_null() throws Exception
  {
    /* prepare */

    /* execute */
    boolean result = sut.hasLinksToShowcase();

    /* assert */
    assertThat(result).isFalse();
  }

  @Test
  public void hasLinksToShowcase() throws Exception
  {
    /* prepare */
    sut.getShowcase().addEmptyLinkToShowcase();

    /* execute */
    boolean result = sut.hasLinksToShowcase();

    /* assert */
    assertThat(result).isTrue();
  }

  @Test
  public void hasLinkToSourcesUrl_null() throws Exception
  {
    /* prepare */

    /* execute */
    boolean result = sut.hasLinkToSourcesUrl();

    /* assert */
    assertThat(result).isFalse();
  }

  @Test
  public void userHasEditPermission_liferayUser_null() throws Exception
  {
    /* prepare */
    currentUser.setLiferayUser(null);

    /* execute */
    boolean result = sut.userHasEditPermission();

    /* assert */
    assertThat(result).isFalse();
  }

  @Test
  public void isShowcaseVisibleForUser_liferayUser_null() throws Exception
  {
    /* prepare */
    currentUser.setLiferayUser(null);

    /* execute */
    boolean result = sut.isShowcaseVisibleForUser();

    /* assert */
    assertThat(result).isFalse();
  }

  @Test
  public void getCategories_null() throws Exception
  {
    /* prepare */

    /* execute */
    List<Category> result = sut.getCategories();

    /* assert */
    assertThat(result).isEmpty();
    assertThat(sut.getShowcase().getCategories()).isNull();
  }

  @Test
  public void getCategories_categoryMap_null() throws Exception
  {
    /* prepare */
    List<de.seitenbau.govdata.edit.model.Category> categories = new ArrayList<>();
    categories.add(new de.seitenbau.govdata.edit.model.Category());
    categories.add(new de.seitenbau.govdata.edit.model.Category(null, ""));
    categories.add(new de.seitenbau.govdata.edit.model.Category(1L, "envi"));
    showcaseViewModel.setCategories(categories);
    sut = new SelectedShowcase(showcaseViewModel, currentUser, null);
    int size = categories.size();

    /* execute */
    List<Category> result = sut.getCategories();

    /* assert */
    assertThat(result).isEmpty();
    assertThat(sut.getCategoryMap()).isNull();
    assertThat(sut.getShowcase().getCategories()).hasSize(size);
  }

  @Test
  public void getCategories() throws Exception
  {
    /* prepare */
    List<de.seitenbau.govdata.edit.model.Category> categories = new ArrayList<>();
    categories.add(new de.seitenbau.govdata.edit.model.Category());
    categories.add(new de.seitenbau.govdata.edit.model.Category(null, ""));
    categories.add(new de.seitenbau.govdata.edit.model.Category(1L, "envi"));
    categories.add(new de.seitenbau.govdata.edit.model.Category(2L, "heal"));
    sut.getShowcase().setCategories(categories);
    sut.getCategoryMap().put("ener", createCategory("ener", "Energie"));
    sut.getCategoryMap().put("envi", createCategory("envi", "Umwelt"));
    sut.getCategoryMap().put("heal", createCategory("heal", "Gesundheit"));
    int size = categories.size();

    /* execute */
    List<Category> result = sut.getCategories();

    /* assert */
    assertThat(result).hasSize(2);
    assertThat(result).extracting("name").containsExactly("envi", "heal");
    assertThat(result).extracting("displayName").containsExactly("Umwelt", "Gesundheit");
    assertThat(sut.getCategoryMap()).isNotEmpty();
    assertThat(sut.getShowcase().getCategories()).hasSize(size);
  }

  @Test
  public void getTagNameList_null() throws Exception
  {
    /* prepare */

    /* execute */
    List<String> result = sut.getTagNameList();

    /* assert */
    assertThat(result).isEmpty();
    assertThat(sut.getShowcase().getKeywords()).isNull();
  }

  @Test
  public void getTagNameList() throws Exception
  {
    /* prepare */
    List<Keyword> keywordList = new ArrayList<>();
    keywordList.add(new Keyword());
    keywordList.add(new Keyword(null, ""));
    keywordList.add(new Keyword(1L, "test<b>bold</b>"));
    keywordList.add(new Keyword(2L, "Test"));
    keywordList.add(new Keyword(3L, "test3"));
    keywordList.add(new Keyword(3L, "test3-2"));
    sut.getShowcase().setKeywords(keywordList);

    /* execute */
    List<String> result = sut.getTagNameList();

    /* assert */
    assertThat(result).containsExactly("testbold", "Test", "test3", "test3-2");
  }

  private Category createCategory(String name, String displayName)
  {
    GroupBean bean = new GroupBean();
    bean.setName(name);
    bean.setDisplayName(displayName);
    return new CategoryImpl(bean);
  }
}
