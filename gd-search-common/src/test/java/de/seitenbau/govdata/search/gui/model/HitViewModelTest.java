package de.seitenbau.govdata.search.gui.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class HitViewModelTest
{
  HitViewModel sut;

  @Before
  public void setup()
  {
    sut = new HitViewModel();
  }

  @Test
  public void getDifferentFormatTypes_null()
  {
    /* prepare */

    /* execute */
    List<String> result = sut.getDifferentFormatTypes();

    /* assert */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void getDifferentFormatTypes()
  {
    /* prepare */
    ResourceViewModel res1 = ResourceViewModel.builder().format("CSV").build();
    ResourceViewModel res2 = ResourceViewModel.builder().format("CSV").build();
    ResourceViewModel res3 = ResourceViewModel.builder()
        .format("https://publications.europa.eu/resource/authority/file-type/CSV").build();
    ResourceViewModel res4 = ResourceViewModel.builder()
        .format("https://publications.europa.eu/resource/authority/file-type/XLS").build();
    //
    List<ResourceViewModel> resources = Lists.newArrayList(res1, res2, res3, res4);
    sut.setResources(resources);

    /* execute */
    List<String> result = sut.getDifferentFormatTypes();

    /* assert */
    Assertions.assertThat(result).containsExactly("CSV", "XLS");
  }

  @Test
  public void getPlatformsAsString_null()
  {
    /* prepare */

    /* execute */
    String result = sut.getPlatformsAsString();

    /* assert */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void getPlatformsAsString()
  {
    /* prepare */
    String platformsExpected = "android";
    String platformsUnknown = "test2";
    String platforms = StringUtils.join(platformsExpected, ",", platformsUnknown);
    List<String> platformList = Stream.of(StringUtils.split(platforms, ",")).collect(Collectors.toList());
    sut.setPlatforms(platformList);

    /* execute */
    String result = sut.getPlatformsAsString();

    /* assert */
    Assertions.assertThat(result).isEqualTo(
        StringUtils.join(Stream.of(StringUtils.split(platformsExpected, ","))
            .map(s -> StringUtils.capitalize(s)).collect(Collectors.toList()), ", "));
  }
}
