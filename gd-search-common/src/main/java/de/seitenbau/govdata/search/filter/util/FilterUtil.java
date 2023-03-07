package de.seitenbau.govdata.search.filter.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.seitenbau.govdata.odp.common.filter.SearchConsts;

@Component
public class FilterUtil
{
  @Value("${elasticsearch.default.filter.type:}")
  private String defaultTypeFilterValues;

  @Value("${search.filter.type.disabled:}")
  private String filterTypeDisabled;


  /**
   * Generates a list of the missing filters in property elasticsearch.default.filter.type
   * and the disabled filter in search.filter.type.disabled
   *
   * @return List of all filter types disabled
   */
  public List<String> getFilterDisabledList()
  {
    List<String> disabledFilterTypes =
        Arrays.stream(StringUtils.stripAll(StringUtils.splitByWholeSeparator(filterTypeDisabled, ",")))
            .collect(Collectors.toList());

    disabledFilterTypes.addAll(SearchConsts.VALID_FILTER_TYPES_WITHOUT_ALL_ORDERED.stream()
        .filter(s -> !getDefaultTypeFilterValues().contains(s)).collect(Collectors.toList()));
    return disabledFilterTypes;
  }


  /**
   * Generates a list from the value of property elasticsearch.default.filter.type
   *
   * @return List of the default filter types
   */
  public List<String> getDefaultTypeFilterValues()
  {
    String[] defaultTypeFilterValuesArray =
        StringUtils.stripAll(StringUtils.splitByWholeSeparator(defaultTypeFilterValues, ","));

    return Arrays.stream(defaultTypeFilterValuesArray).collect(Collectors.toList());
  }
}
