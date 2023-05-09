package de.seitenbau.govdata.search.filter;

import static org.junit.Assert.assertNotNull;

import org.assertj.core.api.Assertions;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import de.seitenbau.govdata.search.util.states.BoundingBoxContainer;

public class BoundingBoxTest
{
  private BoundingBox sutFilter;
  
  @Test
  public void createFilter_relation_default() throws Exception
  {
    /* prepare */
    sutFilter = new BoundingBox("searchField", "filterFragmentName",
        "9.010694329052733,47.62843023858082,9.304647270947264,47.71165977896959");

    /* execute */
    QueryBuilder result = sutFilter.createFilter();

    /* assert */
    assertNotNull(result);
    Assertions.assertThat(result.queryName()).isNull();
    assertQueryString(result, ShapeRelation.INTERSECTS);
  }

  @Test
  public void createFilter_relation_intersects() throws Exception
  {
    /* prepare */
    sutFilter = new BoundingBox("searchField", "filterFragmentName",
        "9.010694329052733,47.62843023858082,9.304647270947264,47.71165977896959", ShapeRelation.INTERSECTS);

    /* execute */
    QueryBuilder result = sutFilter.createFilter();

    /* assert */
    assertNotNull(result);
    Assertions.assertThat(result.queryName()).isNull();
    assertQueryString(result, ShapeRelation.INTERSECTS);
  }

  @Test
  public void createFilter_relation_within() throws Exception
  {
    /* prepare */
    sutFilter = new BoundingBox("searchField", "filterFragmentName",
        "9.010694329052733,47.62843023858082,9.304647270947264,47.71165977896959", ShapeRelation.WITHIN);

    /* execute */
    QueryBuilder result = sutFilter.createFilter();

    /* assert */
    assertNotNull(result);
    Assertions.assertThat(result.queryName()).isNull();
    assertQueryString(result, ShapeRelation.WITHIN);
  }

  @Test
  public void createFilter_BoundingBoxContainer_relation_within() throws Exception
  {
    /* prepare */
    BoundingBoxContainer container =
        BoundingBoxContainer.builder().latitudeMax(47.71165977896959).latitudeMin(47.62843023858082)
            .longitudeMax(9.304647270947264).longitudeMin(9.010694329052733).build();
    sutFilter = new BoundingBox("searchField", "filterFragmentName", container, ShapeRelation.WITHIN);

    /* execute */
    QueryBuilder result = sutFilter.createFilter();

    /* assert */
    assertNotNull(result);
    Assertions.assertThat(result.queryName()).isNull();
    assertQueryString(result, ShapeRelation.WITHIN);
  }

  private void assertQueryString(QueryBuilder result, ShapeRelation relation)
  {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(result);
    String queryString = sourceBuilder.toString();
    Assertions.assertThat(queryString).contains("[9.010694329052733,47.71165977896959]");
    Assertions.assertThat(queryString).contains("[9.304647270947264,47.62843023858082]");
    Assertions.assertThat(queryString).contains("\"relation\":\"" + relation.getRelationName() + "\"");
  }

}
