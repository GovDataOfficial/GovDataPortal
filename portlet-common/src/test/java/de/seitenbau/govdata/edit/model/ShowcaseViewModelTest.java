package de.seitenbau.govdata.edit.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.seitenbau.govdata.common.showcase.model.ShowcasePlatformEnum;
import de.seitenbau.govdata.common.showcase.model.ShowcaseTypeEnum;
import de.seitenbau.govdata.db.api.model.Showcase;
import de.seitenbau.govdata.edit.constants.EditCommonConstants;
import de.seitenbau.govdata.edit.mapper.ShowcaseMapper;
import de.seitenbau.govdata.testing.db.api.testdata.TestDataFactory;

public class ShowcaseViewModelTest
{
  private TestDataFactory testDataFactory = new TestDataFactory();

  private ShowcaseViewModel sut;

  @Before
  public void setup() throws Exception
  {
    sut = new ShowcaseViewModel();

    FileUtils.deleteQuietly(EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET.toFile());
  }

  @Test
  public void defaultValues()
  {
    /* prepare */

    /* execute */

    /* assert */
    Assertions.assertThat(sut.isInEditing()).isFalse();
    Assertions.assertThat(sut.isPrivate()).isTrue();
    Assertions.assertThat(sut.getTitle()).isNull();
    Assertions.assertThat(sut.getNotes()).isNull();
    Assertions.assertThat(sut.getContact()).isNull();
    Assertions.assertThat(sut.getPrimaryShowcaseType()).isNull();
    Assertions.assertThat(sut.getImages()).isNull();
    Assertions.assertThat(sut.getAdditionalShowcaseTypes()).isNull();
    Assertions.assertThat(sut.getCategories()).isNull();
    Assertions.assertThat(sut.getKeywords()).isNull();
    Assertions.assertThat(sut.getLinksToShowcase()).isNull();
    Assertions.assertThat(sut.getPlatforms()).isNull();
    Assertions.assertThat(sut.getUsedDatasets()).isNull();
    Assertions.assertThat(sut.getSelectedAdditionalShowcaseTypes()).isNull();
    Assertions.assertThat(sut.getSelectedCategories()).isNull();
    Assertions.assertThat(sut.getSelectedPlatforms()).isNull();
    Assertions.assertThat(sut.getTags()).isNull();
    Assertions.assertThat(sut.getSelectedPrimaryShowcaseType()).isNull();
  }

  @Test
  public void initNewModel()
  {
    /* prepare */

    /* execute */
    sut.initNewModel();

    /* assert */
    Assertions.assertThat(sut.isInEditing()).isTrue();
    Assertions.assertThat(sut.isPrivate()).isTrue();
    Assertions.assertThat(sut.getTitle()).isNull();
    Assertions.assertThat(sut.getNotes()).isNull();
    Assertions.assertThat(sut.getContact()).isNotNull();
    Assertions.assertThat(sut.getPrimaryShowcaseType()).isNull();
    Assertions.assertThat(sut.getImages()).hasSize(4);
    Assertions.assertThat(countEmptyImages(sut.getImages())).isEqualTo(4);
    Assertions.assertThat(countEmptyThumbnailImages(sut.getImages())).isEqualTo(4);
    Assertions.assertThat(sut.getAdditionalShowcaseTypes()).isNull();
    Assertions.assertThat(sut.getCategories()).isNull();
    Assertions.assertThat(sut.getKeywords()).isNull();
    Assertions.assertThat(sut.getLinksToShowcase()).hasSize(1);
    Assertions.assertThat(sut.getLinksToShowcase().get(0).hasValues()).isFalse();
    Assertions.assertThat(sut.getPlatforms()).isNull();
    Assertions.assertThat(sut.getUsedDatasets()).hasSize(1);
    Assertions.assertThat(sut.getUsedDatasets().get(0).hasValues()).isFalse();
    Assertions.assertThat(sut.getSelectedAdditionalShowcaseTypes()).isNull();
    Assertions.assertThat(sut.getSelectedCategories()).isNull();
    Assertions.assertThat(sut.getSelectedPlatforms()).isNull();
    Assertions.assertThat(sut.getTags()).isNull();
    Assertions.assertThat(sut.getSelectedPrimaryShowcaseType()).isNull();
  }

  @Test
  public void initModel_empty()
  {
    /* prepare */

    /* execute */
    sut.initModel();

    /* assert */
    Assertions.assertThat(sut.isInEditing()).isTrue();
    Assertions.assertThat(sut.isPrivate()).isTrue();
    Assertions.assertThat(sut.getTitle()).isNull();
    Assertions.assertThat(sut.getNotes()).isNull();
    Assertions.assertThat(sut.getContact()).isNotNull();
    Assertions.assertThat(sut.getPrimaryShowcaseType()).isNull();
    Assertions.assertThat(sut.getImages()).hasSize(4);
    Assertions.assertThat(countEmptyImages(sut.getImages())).isEqualTo(4);
    Assertions.assertThat(countEmptyThumbnailImages(sut.getImages())).isEqualTo(4);
    Assertions.assertThat(sut.getAdditionalShowcaseTypes()).isNull();
    Assertions.assertThat(sut.getCategories()).isNull();
    Assertions.assertThat(sut.getKeywords()).isNull();
    Assertions.assertThat(sut.getLinksToShowcase()).hasSize(1);
    Assertions.assertThat(sut.getLinksToShowcase().get(0).hasValues()).isFalse();
    Assertions.assertThat(sut.getPlatforms()).isNull();
    Assertions.assertThat(sut.getUsedDatasets()).hasSize(1);
    Assertions.assertThat(sut.getUsedDatasets().get(0).hasValues()).isFalse();
    Assertions.assertThat(sut.getSelectedAdditionalShowcaseTypes()).isEmpty();
    Assertions.assertThat(sut.getSelectedCategories()).isEmpty();
    Assertions.assertThat(sut.getSelectedPlatforms()).isEmpty();
    Assertions.assertThat(sut.getTags()).isEmpty();
    Assertions.assertThat(sut.getSelectedPrimaryShowcaseType()).isNull();
  }

  @Test
  public void initModel()
  {
    /* prepare */
    Showcase showcase = testDataFactory.getTestShowcaseEntityWithIds();
    sut = ShowcaseMapper.mapShowcaseEntityToViewModel(showcase);

    /* execute */
    sut.initModel();

    /* assert */
    Assertions.assertThat(sut.isInEditing()).isTrue();
    Assertions.assertThat(sut.isPrivate()).isFalse();
    Assertions.assertThat(sut.getTitle()).isNotNull();
    Assertions.assertThat(sut.getNotes()).isNotNull();
    Assertions.assertThat(sut.getContact()).isNotNull();
    Assertions.assertThat(sut.getPrimaryShowcaseType()).isNotNull();
    // Check not only empty images -> byte[] not empty
    Assertions.assertThat(sut.getImages()).hasSize(4);
    Assertions.assertThat(countEmptyImages(sut.getImages())).isEqualTo(3);
    Assertions.assertThat(countEmptyThumbnailImages(sut.getImages())).isEqualTo(3);
    Assertions.assertThat(sut.getAdditionalShowcaseTypes()).isNotEmpty();
    Assertions.assertThat(sut.getCategories()).isNotEmpty();
    Assertions.assertThat(sut.getKeywords()).isNotEmpty();
    Assertions.assertThat(sut.getLinksToShowcase()).hasSize(2);
    Assertions.assertThat(countEmptyLinks(sut.getLinksToShowcase())).isEqualTo(1);
    Assertions.assertThat(sut.getPlatforms()).isNotEmpty();
    Assertions.assertThat(sut.getUsedDatasets()).hasSize(2);
    Assertions.assertThat(countEmptyLinks(sut.getUsedDatasets())).isEqualTo(1);
    Assertions.assertThat(sut.getSelectedAdditionalShowcaseTypes()).isNotEmpty();
    Assertions.assertThat(sut.getSelectedCategories()).isNotEmpty();
    Assertions.assertThat(sut.getSelectedPlatforms()).isNotEmpty();
    Assertions.assertThat(sut.getTags()).isNotEmpty();
    Assertions.assertThat(sut.getSelectedPrimaryShowcaseType()).isNotEmpty();
  }

  @Test
  public void updateModel_empty() throws Exception
  {
    /* prepare */

    /* execute */
    sut.updateModel();

    /* assert */
    Assertions.assertThat(sut.isInEditing()).isFalse();
    Assertions.assertThat(sut.isPrivate()).isTrue();
    Assertions.assertThat(sut.getTitle()).isNull();
    Assertions.assertThat(sut.getNotes()).isNull();
    Assertions.assertThat(sut.getContact()).isNull();
    Assertions.assertThat(sut.getPrimaryShowcaseType()).isNull();
    Assertions.assertThat(sut.getImages()).isEmpty();
    Assertions.assertThat(sut.getAdditionalShowcaseTypes()).isEmpty();
    Assertions.assertThat(sut.getCategories()).isEmpty();
    Assertions.assertThat(sut.getKeywords()).isEmpty();
    Assertions.assertThat(sut.getLinksToShowcase()).isEmpty();
    Assertions.assertThat(sut.getPlatforms()).isEmpty();
    Assertions.assertThat(sut.getUsedDatasets()).isEmpty();
    Assertions.assertThat(sut.getSelectedAdditionalShowcaseTypes()).isNull();
    Assertions.assertThat(sut.getSelectedCategories()).isNull();
    Assertions.assertThat(sut.getSelectedPlatforms()).isNull();
    Assertions.assertThat(sut.getTags()).isNull();
    Assertions.assertThat(sut.getSelectedPrimaryShowcaseType()).isNull();
  }

  @Test
  public void updateModel() throws Exception
  {
    /* prepare */
    Showcase showcase = testDataFactory.getTestShowcaseEntityWithIds();
    sut = ShowcaseMapper.mapShowcaseEntityToViewModel(showcase);
    sut.initModel();
    List<Platform> selectedPlatforms =
        Lists.newArrayList(new Platform(1L, ShowcasePlatformEnum.ANDROID.getField()),
            new Platform(null, ShowcasePlatformEnum.LINUX.getField()));
    List<String> selectedPlatformsStringList = Collections.unmodifiableList(
        selectedPlatforms.stream().map(o -> o.getName()).collect(Collectors.toList()));
    List<Type> selectedAdditionalTypes =
        Lists.newArrayList(new Type(2L, ShowcaseTypeEnum.APP.getField()),
            new Type(null, ShowcaseTypeEnum.TOOL.getField()));
    List<String> selectedAdditionalTypesStringList =
        Collections.unmodifiableList(
            selectedAdditionalTypes.stream().map(o -> o.getName()).collect(Collectors.toList()));
    List<Category> selectedCategories =
        Lists.newArrayList(new Category(1L, "envi"), new Category(null, "cat2"));
    List<String> selectedCategoriesStringList = Collections.unmodifiableList(
        selectedCategories.stream().map(o -> o.getName()).collect(Collectors.toList()));
    List<Keyword> selectedKeywords =
        Lists.newArrayList(new Keyword(1L, "Key1"), new Keyword(null, "tag2"));
    String selectedKeywordsString =
        StringUtils.join(selectedKeywords.stream().map(o -> o.getName()).collect(Collectors.toList()), ", ");
    sut.setSelectedAdditionalShowcaseTypes(selectedAdditionalTypesStringList);
    sut.setSelectedPlatforms(selectedPlatformsStringList);
    sut.setSelectedCategories(selectedCategoriesStringList);
    sut.setSelectedPrimaryShowcaseType(ShowcaseTypeEnum.CONCEPT.getField());
    sut.setTags(selectedKeywordsString);
    sut.getLinksToShowcase().add(new Link(null, "link-name", "link-url"));
    sut.getLinksToShowcase().add(new Link(null, "", ""));
    sut.getUsedDatasets().add(new Link(null, "dataset-name", "dataset-url"));
    sut.getUsedDatasets().add(new Link(null, "", ""));
    Path tmpDir = Files.createDirectories(EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET);
    Path tempFile = Files.createTempFile(tmpDir, "", ".png");
    Files.write(tempFile, testDataFactory.testImg);
    sut.getImages().add(new Image(null, 2, null, null, null, tempFile.getFileName().toString(), ""));

    /* execute */
    sut.updateModel();

    /* assert */
    Assertions.assertThat(sut.isInEditing()).isFalse();
    Assertions.assertThat(sut.isPrivate()).isFalse();
    Assertions.assertThat(sut.getTitle()).isNotNull();
    Assertions.assertThat(sut.getNotes()).isNotNull();
    Assertions.assertThat(sut.getContact()).isNotNull();
    Assertions.assertThat(sut.getPrimaryShowcaseType())
        .isEqualTo(new Type(1L, ShowcaseTypeEnum.CONCEPT.getField()));
    // Check for only not empty images -> byte[] not empty
    Assertions.assertThat(sut.getImages()).hasSize(2);
    Assertions.assertThat(sut.getImages()).extracting("id", Long.class).containsExactly(1L, null);
    Assertions.assertThat(countEmptyImages(sut.getImages())).isZero();
    Assertions.assertThat(countEmptyThumbnailImages(sut.getImages())).isEqualTo(1);
    Assertions.assertThat(sut.getAdditionalShowcaseTypes()).containsOnly(
        selectedAdditionalTypes.stream().toArray(value -> new Type[value]));
    Assertions.assertThat(sut.getCategories()).containsOnly(
        selectedCategories.stream().toArray(value -> new Category[value]));
    Assertions.assertThat(sut.getKeywords()).containsOnly(
        selectedKeywords.stream().toArray(value -> new Keyword[value]));
    Assertions.assertThat(sut.getLinksToShowcase()).hasSize(2);
    Link linkToShowcase = sut.getLinksToShowcase().get(0);
    Assertions.assertThat(linkToShowcase.getId()).isEqualTo(1L);
    Assertions.assertThat(linkToShowcase.hasValues()).isTrue();
    linkToShowcase = sut.getLinksToShowcase().get(1);
    Assertions.assertThat(linkToShowcase.getId()).isNull();
    Assertions.assertThat(linkToShowcase.hasValues()).isTrue();
    Assertions.assertThat(sut.getPlatforms()).containsOnly(
        selectedPlatforms.stream().toArray(value -> new Platform[value]));
    Assertions.assertThat(sut.getUsedDatasets()).hasSize(2);
    Link usedDataset = sut.getUsedDatasets().get(0);
    Assertions.assertThat(usedDataset.getId()).isEqualTo(1L);
    Assertions.assertThat(usedDataset.hasValues()).isTrue();
    usedDataset = sut.getUsedDatasets().get(1);
    Assertions.assertThat(usedDataset.getId()).isNull();
    Assertions.assertThat(usedDataset.hasValues()).isTrue();
    Assertions.assertThat(sut.getSelectedAdditionalShowcaseTypes())
        .isEqualTo(selectedAdditionalTypesStringList);
    Assertions.assertThat(sut.getSelectedCategories()).isEqualTo(selectedCategoriesStringList);
    Assertions.assertThat(sut.getSelectedPlatforms()).isEqualTo(selectedPlatformsStringList);
    Assertions.assertThat(sut.getTags()).isEqualTo(selectedKeywordsString);
    Assertions.assertThat(sut.getSelectedPrimaryShowcaseType())
        .isEqualTo(ShowcaseTypeEnum.CONCEPT.getField());
    Assertions.assertThat(Files.exists(tempFile)).isFalse();
  }

  @Test
  public void updateModel_notes_html() throws Exception
  {
    /* prepare */
    Showcase showcase = testDataFactory.getTestShowcaseEntityWithIds();
    sut = ShowcaseMapper.mapShowcaseEntityToViewModel(showcase);
    String notesWithHTML = "<p>Hello there</p>\ntext and more <i>text</i> with <b>bold</b> \r\nand a "
        + "<u>underline</u>.";
    sut.setNotes(notesWithHTML);

    /* execute */
    sut.updateModel();

    /* assert */
    Assertions.assertThat(sut.getNotes()).isEqualTo("<p>Hello there</p>"
        + "<br>text and more <i>text</i> with <b>bold</b> <br>and a <u>underline</u>.");
  }

  @Test
  public void initModel_notes_html() throws Exception
  {
    /* prepare */
    Showcase showcase = testDataFactory.getTestShowcaseEntityWithIds();
    sut = ShowcaseMapper.mapShowcaseEntityToViewModel(showcase);
    String notesWithHTML = "<p>Hello there</p><br />text and more <i>text</i> with <b>bold</b> <br>\nand a "
        + "<u>underline</u>.";
    sut.setNotes(notesWithHTML);

    /* execute */
    sut.initModel();

    /* assert */
    Assertions.assertThat(sut.getNotes()).isEqualTo("<p>Hello there</p>"
        + "\ntext and more <i>text</i> with <b>bold</b> \n\nand a <u>underline</u>.");
  }

  @Test
  public void removeImage_indexGreaterThanSize() throws Exception
  {
    /* prepare */
    int index = 1;
    sut.setImages(Lists.newArrayList(new Image()));

    /* execute */
    sut.removeImage(index);

    /* assert */
    Assertions.assertThat(sut.getImages()).hasSize(1);
  }

  @Test
  public void removeImage_thumbnail() throws Exception
  {
    /* prepare */
    int index = 1;
    sut.setImages(Lists.newArrayList(new Image()));
    Image toAdd = new Image();
    toAdd.setImageOrderId(2);
    toAdd.setThumbnailImage("gjkfdkgjfdlgj");
    sut.getImages().add(index, toAdd);

    /* execute */
    sut.removeImage(index);

    /* assert */
    Assertions.assertThat(sut.getImages()).hasSize(4);
    Assertions.assertThat(sut.getImages().get(index).getTmpFileName()).isNull();
  }

  @Test
  public void removeImage_tempFile_doesNotExists() throws Exception
  {
    /* prepare */
    int index = 1;
    sut.setImages(Lists.newArrayList(new Image()));
    Image toAdd = new Image();
    toAdd.setImageOrderId(2);
    toAdd.setTmpFileName("notexists.png");
    sut.getImages().add(index, toAdd);

    /* execute */
    sut.removeImage(index);

    /* assert */
    Assertions.assertThat(sut.getImages()).hasSize(4);
    Assertions.assertThat(sut.getImages().get(index).getTmpFileName()).isNull();
  }

  @Test
  public void removeImage_tempFile_exists() throws Exception
  {
    /* prepare */
    int index = 1;
    sut.setImages(Lists.newArrayList(new Image()));
    Path tmpDir = Files.createDirectories(EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET);
    Path tempFile = Files.createTempFile(tmpDir, "", ".png");
    Files.write(tempFile, testDataFactory.testImg);
    Image toAdd = new Image();
    toAdd.setImageOrderId(2);
    toAdd.setTmpFileName(tempFile.getFileName().toString());
    sut.getImages().add(index, toAdd);

    /* execute */
    sut.removeImage(index);

    /* assert */
    Assertions.assertThat(sut.getImages()).hasSize(4);
    Assertions.assertThat(Files.exists(tempFile)).isFalse();
  }

  private int countEmptyImages(List<Image> images)
  {
    int result = 0;
    for (Image image : images)
    {
      if (ArrayUtils.isEmpty(image.getImage()))
      {
        result++;
      }
    }
    return result;
  }

  private int countEmptyThumbnailImages(List<Image> images)
  {
    int result = 0;
    for (Image image : images)
    {
      if (StringUtils.isEmpty(image.getThumbnailImage()))
      {
        result++;
      }
    }
    return result;
  }

  private int countEmptyLinks(List<Link> links)
  {
    int result = 0;
    for (Link link : links)
    {
      if (!link.hasValues())
      {
        result++;
      }
    }
    return result;
  }
}
