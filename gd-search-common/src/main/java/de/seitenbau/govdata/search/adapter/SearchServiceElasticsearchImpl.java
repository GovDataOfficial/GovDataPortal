package de.seitenbau.govdata.search.adapter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.naming.ConfigurationException;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.Suggest.Suggestion;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.DirectCandidateGeneratorBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.common.client.TrustAllStrategy;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.search.common.ESFieldConsts;
import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.common.SearchQuery;
import de.seitenbau.govdata.search.filter.BaseFilter;
import de.seitenbau.govdata.search.filter.util.FilterUtil;
import de.seitenbau.govdata.search.index.IndexConstants;
import de.seitenbau.govdata.search.index.mapper.SearchHitMapper;
import de.seitenbau.govdata.search.index.model.FacetDto;
import de.seitenbau.govdata.search.index.model.FilterListDto;
import de.seitenbau.govdata.search.index.model.HitDto;
import de.seitenbau.govdata.search.index.model.SearchResultContainer;
import de.seitenbau.govdata.search.index.model.SuggestionOption;
import de.seitenbau.govdata.search.sort.Sort;
import de.seitenbau.govdata.search.sort.SortType;
import lombok.extern.slf4j.Slf4j;

/**
 * Führt Aktionen (Suche, Vorschläge, Speicherung der Suchwörter) gegen die Elasticsearch aus.
 *
 * @author arothbauer
 * @author rnoerenberg
 * @author tscheffler
 */
@Slf4j
@Service
public class SearchServiceElasticsearchImpl implements SearchService
{
  private static final String FIELD_TIMESTAMP = "timestamp";

  private static final String FIELD_HISTORYITEM_PHRASE = "phrase";

  private static final String SUGGEST_DID_YOU_MEAN = "did_you_mean";

  private static final int DEFAULT_SEARCH_RESULT_SIZE = 10;

  private static final String SAYT_COMPLETION_SUGGESTION = "search-as-you-type";

  private static final int SEARCH_PHRASE_MIN_LENGTH = 2;

  /**
   * not sure which unit... but the value given by the "area" package in python, used to calculate
   * our index areas
   */
  private static final double AREA_GERMANY = 366786359054.79553;

  /**
   * Maximum distance between boundingbox-center and a datasets spatial center. This is approx. half
   * of the maximal distance two points in Germany can have (The ranked dataset has to be inside the
   * boundingbox. So assuming the boundingbox is only be as big as whole germany, the center is at
   * most half of Germanys size away from the dataset's center.)
   */
  private static final String MAX_RADIUS_GERMANY = "450km";

  private RestHighLevelClient client;

  @Value("${elasticsearch.user}")
  private String esUser;

  @Value("${elasticsearch.userpass}")
  private String esUserPass;

  @Value("${elasticsearch.cluster}")
  private String esCluster;

  @Value("${elasticsearch.scheme}")
  private String esScheme;

  private String[] nodes;

  private String[] indexNames;

  private int minutesKeepAliveScroll;

  @Inject
  private FilterUtil filterUtil;

  private String[] highValueDatasetTags;

  @Autowired
  private SearchHitMapper searchHitMapper;

  @Autowired
  private CategoryCache categoryCache;

  @Value("${elasticsearch.liferay.index.name}")
  private String liferayIndexName;

  @Value("${elasticsearch.search.index.searchhistory}")
  private String searchhistoryIndexName;

  @Value("${elasticsearch.search.index.metrics}")
  private String metricIndexName;

  @Value("${elasticsearch.search.spatial_area_boost}")
  private float spatialAreaBoost;

  @Value("${elasticsearch.search.spatial_center_boost}")
  private float spatialCenterBoost;

  /**
   * Initialisiert die benötigten Objekte.
   *
   * @throws ConfigurationException bei einem Fehler in der Konfiguration.
   * @throws UnknownHostException
   * @throws NumberFormatException
   * @throws KeyStoreException
   * @throws NoSuchAlgorithmException
   * @throws KeyManagementException
   */
  @SuppressWarnings("deprecation")
  @PostConstruct
  public void init() throws ConfigurationException, NumberFormatException, UnknownHostException,
      NoSuchAlgorithmException, KeyStoreException, KeyManagementException
  {
    // Read nodes
    List<HttpHost> httpHosts = new ArrayList<>();
    for (String host : nodes)
    {
      String[] hostAndPort = host.trim().split(":");
      if (hostAndPort.length != 2)
      {
        String msg = "Invalid host configuration '" + host + "', expected [hostname]:[port]";
        log.error(msg);
        throw new ConfigurationException(msg);
      }
      httpHosts.add(new HttpHost(hostAndPort[0], Integer.parseInt(hostAndPort[1]), esScheme));
    }

    // Init SSL Settings
    SSLContextBuilder sslBuilder = new SSLContextBuilder();
    sslBuilder.loadTrustMaterial(null, new TrustAllStrategy());
    final SSLContext sslContext = sslBuilder.build();

    // Init credentials for elastic
    final CredentialsProvider credentialsProvider =
        new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY,
        new UsernamePasswordCredentials(esUser, esUserPass));

    // Build client
    RestClientBuilder builder = RestClient.builder(
        httpHosts.toArray(new HttpHost[0]))
        .setHttpClientConfigCallback(
            httpClientBuilder -> {
              httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
              httpClientBuilder.setSSLContext(sslContext);
              httpClientBuilder
                  .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
              return httpClientBuilder;
            });
    client = new RestHighLevelClient(builder);
  }

  /**
   * Räumt die initialisierten Objekte wieder auf, zum Beispiel schließen von offenen Verbindungen.
   * @throws IOException
   */
  @PreDestroy
  public void destroy() throws IOException
  {
    client.close();
  }

  /**
   * Führt eine Suche mit den angegebenen Suchbegriffen und Filtern aus und gibt das Ergebnis
   * zurück.
   *
   * @param q der Suchbegriff.
   * @param numResults die Anzahl der maximalen Treffer, die zurückgeliefert werden sollen. Falls
   *        null übergeben wird das Default von {@value #DEFAULT_SEARCH_RESULT_SIZE} genutzt.
   * @param bundle die anzuwendenden Filter.
   * @param sort die Sortierung.
   * @return die Treffer mit zusätzlichen Informationen wie Facetten.
   * @see de.seitenbau.govdata.search.adapter.SearchService#search(java.lang.String,
   *      java.lang.Integer, de.seitenbau.govdata.search.common.SearchFilterBundle,
   *      de.seitenbau.govdata.search.sort.Sort)
   */
  @Override
  @SuppressWarnings("deprecation")
  public SearchResultContainer search(
      SearchQuery q, Integer numResults, SearchFilterBundle bundle, Sort sort)
  {
    final String method = "search() : ";
    log.trace(method + "Start");

    // Map of active Filters, used in result presentation
    Map<String, FilterListDto> activeFilterMap = new HashMap<>();

    // will contain all filters except for document types (is handled separately)
    BoolQueryBuilder filters = QueryBuilders.boolQuery();

    // Create all supplied filters from their transport objects
    for (BaseFilter filter : bundle.getFilters())
    {
      // state search is build separately
      if (!Objects.equals(filter.getFragmentName(), SearchConsts.FACET_STATE))
      {
        filters.filter(filter.createFilter());
      }
    }

    // query for state search
    final QueryBuilder stateQuery = buildStateSearchQuery(bundle.getFilters().stream()
        .filter(f -> Objects.equals(f.getFragmentName(), SearchConsts.FACET_STATE)).findFirst());

    // filter on certain types
    QueryBuilder typeFilter = createTypeFilter(bundle.getTypeFilter());

    // check if private or public showcases are shown
    if (bundle.getShowOnlyPrivateShowcases())
    {
      // filter for private
      QueryBuilder privateFilter =
          QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(ESFieldConsts.PRIVATE, true));

      // filter for showcases only
      QueryBuilder typeShowcaseFilter =
          QueryBuilders.boolQuery()
              .filter(QueryBuilders.termQuery(ESFieldConsts.FIELD_METADATA_TYPE,
                  IndexConstants.INDEX_TYPE_SHOWCASE));

      filters.filter(privateFilter);
      filters.filter(typeShowcaseFilter);
    }
    // filter private datasets (deleted / not published)
    else if (bundle.getHidePrivateDatasets())
    {
      filters.filter(getPrivateFilter());
    }

    // aggregate statistics for filter-facettes
    List<AggregationBuilder> aggregations = buildAggregations();

    // combine filters (adding AND to AND seems stupid, but we need "filters" unmodified later)
    BoolQueryBuilder combinedFilters = QueryBuilders.boolQuery().filter(filters);
    if (typeFilter != null)
    {
      combinedFilters.filter(typeFilter);
    }

    final QueryBuilder baseQuery = QueryBuilders.boolQuery().must(buildBaseQuery(q)).must(stateQuery);
    final QueryBuilder filteredQuery = QueryBuilders.boolQuery().must(baseQuery).filter(combinedFilters);

    // Prepare Search
    int numberOfResults = DEFAULT_SEARCH_RESULT_SIZE;
    if (numResults != null)
    {
      numberOfResults = numResults;
    }
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.from(0);
    sourceBuilder.size(numberOfResults);
    sourceBuilder.explain(false);

    // Add Query
    if (bundle.getBoostSpatialRelevance() && (spatialAreaBoost > 0 || spatialCenterBoost > 0))
    {
      // if boostSpatialRelevance is active (and weights are enabled), wrap the query in a function
      // score query
      FilterFunctionBuilder[] scoreFunctions = {
          new FunctionScoreQueryBuilder.FilterFunctionBuilder(
              ScoreFunctionBuilders.linearDecayFunction("metadata.spatial_area", 0, AREA_GERMANY)
                  .setWeight(spatialAreaBoost)),
          new FunctionScoreQueryBuilder.FilterFunctionBuilder(
              ScoreFunctionBuilders.linearDecayFunction("metadata.spatial_center", bundle.getSpatialCenter(),
                  MAX_RADIUS_GERMANY).setWeight(spatialCenterBoost))
      };
      FunctionScoreQueryBuilder functionScoreQueryBuilder =
          new FunctionScoreQueryBuilder(filteredQuery, scoreFunctions).boostMode(CombineFunction.SUM);
      sourceBuilder.query(functionScoreQueryBuilder);
    }
    else
    {
      // nothing special, use the filteredQuery as usual
      sourceBuilder.query(filteredQuery);
    }

    // Add Aggregations
    for (AggregationBuilder ags : aggregations)
    {
      // search.addAggregation(ags);
      sourceBuilder.aggregation(ags);
    }

    // Add Sorting
    if (sort != null && sort.getType() != null && sort.getAscending() != null)
    {
      sourceBuilder
          .sort(createSorting(sort, q, bundle.isForceRelevanceSort() || bundle.getBoostSpatialRelevance()));
    }

    SearchRequest searchRequest = new SearchRequest().indices(indexNames).source(sourceBuilder)
        .searchType(SearchType.DFS_QUERY_THEN_FETCH)
        .scroll(new Scroll(TimeValue.timeValueMinutes(minutesKeepAliveScroll)));

    // Only for debugging: prints out query string
    // log.info("### SEARCH QUERY STRING ###");
    // log.info(searchRequest.source().toString());

    SearchResponse response;
    try
    {
      // execute search
      response = client.search(searchRequest, RequestOptions.DEFAULT);
    }
    catch (IOException ex)
    {
      log.warn("{}Elasticsearch-request failed: {}", method, ex.getMessage());
      throw new RuntimeException(ex);
    }

    // Search and count hits per type
    SearchHits responseSearchHits = response.getHits();
    long responseSearchHitsCount = ArrayUtils.getLength(responseSearchHits.getHits());
    TotalHits totalHits = responseSearchHits.getTotalHits();
    if (totalHits == null)
    {
      return SearchResultContainer.builder().build();
    }
    long totalHitsAllTypes = totalHits.value;
    if (typeFilter != null)
    {
      totalHitsAllTypes =
          countHits(baseQuery,
              QueryBuilders.termsQuery(ESFieldConsts.FIELD_METADATA_TYPE,
                  filterUtil.getDefaultTypeFilterValues()),
              filters);
    }

    // Add Type Filters
    activeFilterMap.put(SearchConsts.FILTER_KEY_TYPE, buildTypeFacet(baseQuery, filters, totalHitsAllTypes));

    // Map terms-aggregation results to filterMap
    Aggregations agsResult = response.getAggregations();
    FilterListDto groupAggregateList =
        buildListFacet(SearchConsts.FACET_GROUPS, agsResult.get(SearchConsts.FACET_GROUPS));
    stripUnofficialGroups(groupAggregateList);

    activeFilterMap.put(SearchConsts.FACET_GROUPS,
        groupAggregateList);

    activeFilterMap.put(SearchConsts.FACET_TAGS,
        buildListFacet(SearchConsts.FACET_TAGS, agsResult.get(SearchConsts.FACET_TAGS)));

    activeFilterMap.put(SearchConsts.FACET_FORMAT,
        buildListFacet(SearchConsts.FACET_FORMAT, agsResult.get(SearchConsts.FACET_FORMAT)));

    activeFilterMap.put(SearchConsts.FACET_LICENCE,
        buildListFacet(SearchConsts.FACET_LICENCE, agsResult.get(SearchConsts.FACET_LICENCE)));

    activeFilterMap.put(SearchConsts.FACET_SHOWCASE_TYPE,
        buildListFacet(SearchConsts.FACET_SHOWCASE_TYPE,
            agsResult.get(SearchConsts.FACET_SHOWCASE_TYPE)));

    activeFilterMap.put(SearchConsts.FACET_PLATFORMS,
        buildListFacet(SearchConsts.FACET_PLATFORMS, agsResult.get(SearchConsts.FACET_PLATFORMS)));

    activeFilterMap.put(SearchConsts.FACET_OPENNESS, buildIsOpenFacet(agsResult));

    activeFilterMap.put(SearchConsts.FACET_DATASERVICE,
        buildBoolFilterFacet(agsResult, SearchConsts.FACET_HAS_DATA_SERVICE));

    activeFilterMap.put(SearchConsts.FACET_HIGH_VALUE_DATASET,
        buildBoolFilterFacet(agsResult, SearchConsts.FACET_IS_HIGH_VALUE_DATASET));

    activeFilterMap.put(
        SearchConsts.FACET_SOURCEPORTAL,
        buildListFacet(
            SearchConsts.FACET_SOURCEPORTAL, agsResult.get(SearchConsts.FACET_SOURCEPORTAL)));

    // add all other active Filters to the Map which are not already covered by facets
    for (BaseFilter filter : bundle.getFilters())
    {
      if (!activeFilterMap.containsKey(filter.getFragmentName()))
      {
        FilterListDto simpleFilterList = new FilterListDto();
        // initialize with 0 docs, we won't show this
        simpleFilterList.add(buildFacetDto(filter.getLabel(), 0));
        activeFilterMap.put(filter.getFragmentName(), simpleFilterList);
      }
    }

    // if we have no results, get suggestions
    Set<SuggestionOption> suggestionOptions = new HashSet<>();
    if (totalHits.value == 0 && Objects.nonNull(q) && !q.isEmpty())
    {
      suggestionOptions = querySuggestions(q.getQueryString());
    }

    SearchResultContainer result = SearchResultContainer.builder()
        .moreNextHitsAvailable(totalHits.value > responseSearchHitsCount)
        .pageSize(numberOfResults)
        .hits(mapSearchResult(response))
        .filterMap(activeFilterMap)
        .scrollId(response.getScrollId())
        .suggestions(suggestionOptions)
        .hitsTotal(totalHits.value)
        .build();

    log.trace(method + "End");
    return result;
  }

  @Override
  public SearchResultContainer searchLatest(Integer numResults, String typeFilter)
  {
    SearchFilterBundle searchFilterBundle = new SearchFilterBundle();
    if (typeFilter != null)
    {
      searchFilterBundle.setTypeFilter(typeFilter);
    }

    SearchResultContainer result = search(
        new SearchQuery(null),
        numResults,
        searchFilterBundle,
        new Sort(SortType.LASTMODIFICATION, false));
    return result;
  }

  @Override
  public SearchResultContainer scroll(String scrollId)
  {
    SearchResultContainer result;
    if (scrollId == null)
    {
      log.warn("Scroll-ID nicht mehr in Session vorhanden. Starte neue Suche.");
      throw new IllegalStateException();
    }
    try
    {
      result = scrollNextHits(scrollId);
    }
    catch (SearchPhaseExecutionException invalidScrollId)
    {
      log.warn("Scroll-ID nicht mehr gültig. Starte neue Suche.");
      throw new IllegalStateException();
    }

    return result;
  }

  @SuppressWarnings("deprecation")
  @Override
  public SearchResultContainer scrollNextHits(String scrollId)
  {
    SearchResponse searchResponse;
    try
    {
      searchResponse = client.searchScroll(new SearchScrollRequest(scrollId)
          .scroll(new Scroll(TimeValue.timeValueMinutes(minutesKeepAliveScroll))), RequestOptions.DEFAULT);
    }
    catch (ElasticsearchException | IOException e)
    {
      String msg = String.format("Scroll-ID nicht verarbeitbar! '%s'", scrollId);
      log.info(msg);
      throw new IllegalArgumentException(msg, e);
    }

    SearchResultContainer result = SearchResultContainer.builder()
        .hits(mapSearchResult(searchResponse))
        .scrollId(searchResponse.getScrollId())
        .build();

    return result;
  }

  @SuppressWarnings("deprecation")
  @Override
  public SearchResultContainer singleSearch(List<String> idList, String[] searchIndexes)
  {
    final String method = "singleSearch() : ";
    log.trace(method + "Start");

    // bool query for dataset-name filtering
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

    for (String id : idList)
    {
      // Add each id as should -> Check if one of the values exists
      boolQuery.should(QueryBuilders.matchQuery(
          ESFieldConsts.FIELD_METADATA_NAME, id).operator(Operator.AND));
    }

    // set bool query as BaseQuery
    QueryBuilder baseQuery = boolQuery;

    // add filter for private datasets
    BoolQueryBuilder filters = QueryBuilders.boolQuery();
    filters.filter(getPrivateFilter());

    BoolQueryBuilder combinedFilters = QueryBuilders.boolQuery().filter(filters);
    final QueryBuilder filteredQuery =
        QueryBuilders.boolQuery().must(baseQuery).filter(combinedFilters);

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.from(0);
    sourceBuilder.size(idList.size());
    sourceBuilder.explain(false);
    sourceBuilder.query(filteredQuery);

    SearchRequest searchRequest = new SearchRequest().indices(searchIndexes).source(sourceBuilder)
        .searchType(SearchType.DFS_QUERY_THEN_FETCH);

    // execute search
    SearchResponse response = null;
    try
    {
      response = client.search(searchRequest, RequestOptions.DEFAULT);
    }
    catch (IOException ex)
    {
      log.warn("{}Elasticsearch-request failed: {}", method, ex.getMessage());
      throw new RuntimeException(ex);
    }

    SearchResultContainer result = SearchResultContainer.builder()
        .hits(mapSearchResult(response))
        .build();

    log.trace(method + "End");
    return result;
  }

  @SuppressWarnings("deprecation")
  @Override
  public List<String> findPortalContentIdsByPortletId(String portletId)
  {
    final String method = "findPortalContentIdsByPortletId() : ";
    log.trace(method + "Start");

    MatchAllQueryBuilder baseQuery = QueryBuilders.matchAllQuery();

    QueryBuilder portletIdFilter =
        QueryBuilders.termQuery(ESFieldConsts.FIELD_METADATA_PORTLET_ID, portletId);
    QueryBuilder finalFilter = QueryBuilders.boolQuery().filter(portletIdFilter);

    final QueryBuilder filteredQuery = QueryBuilders.boolQuery().filter(baseQuery).filter(finalFilter);

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.from(0);
    sourceBuilder.size(100);
    sourceBuilder.explain(false);
    sourceBuilder.query(filteredQuery);

    SearchRequest searchRequest = new SearchRequest().indices(liferayIndexName).source(sourceBuilder)
        .searchType(SearchType.DFS_QUERY_THEN_FETCH)
        .scroll(new Scroll(TimeValue.timeValueMinutes(minutesKeepAliveScroll)));

    List<String> contentIds = new ArrayList<>();
    try
    {
      SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

      SearchHit[] hits = response.getHits().getHits();
      while (hits.length != 0)
      {
        for (SearchHit searchHit : hits)
        {
          contentIds.add(searchHit.getId());
        }
        response = client.searchScroll(new SearchScrollRequest(response.getScrollId())
            .scroll(new Scroll(TimeValue.timeValueMinutes(minutesKeepAliveScroll))), RequestOptions.DEFAULT);
        hits = response.getHits().getHits();
      }
    }
    catch (IOException ex)
    {
      log.warn("{}Elasticsearch-request failed: {}", method, ex.getMessage());
    }
    log.trace(method + "End");
    return contentIds;
  }

  @SuppressWarnings("deprecation")
  @Override
  public List<String> findSearchAsYouTypeSuggestions(String q)
  {
    final String method = "findSearchAsYouTypeSuggestions() : ";
    log.trace(method + "Start");

    List<String> suggestions = new ArrayList<>();

    CompletionSuggestionBuilder suggestionBuilder =
        new CompletionSuggestionBuilder("title_suggest");
    suggestionBuilder.text(q);

    for (String indexName : indexNames)
    {
      SuggestBuilder suggestBuilder = new SuggestBuilder();
      suggestBuilder.addSuggestion(SAYT_COMPLETION_SUGGESTION, suggestionBuilder);

      SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
      sourceBuilder.suggest(suggestBuilder);

      SearchRequest searchRequest = new SearchRequest().indices(indexName).source(sourceBuilder);
      SearchResponse response;
      try
      {
        response = client.search(searchRequest, RequestOptions.DEFAULT);
      }
      catch (IOException ex)
      {
        log.warn("{}Elasticsearch-request failed: {}", method, ex.getMessage());
        return suggestions;
      }
      Suggest suggest = response.getSuggest();
      if (suggest != null)
      {
        Suggestion<? extends Entry<? extends Option>> suggestion =
            suggest.getSuggestion(SAYT_COMPLETION_SUGGESTION);
        if (suggestion != null)
        {
          for (Entry<? extends Option> entry : suggestion)
          {
            for (Option option : entry.getOptions())
            {
              if (!suggestions.contains(option.getText().string()))
              {
                suggestions.add(option.getText().string());
              }
            }
          }
        }
      }
    }
    log.trace(method + "End");
    return suggestions;
  }

  @SuppressWarnings("deprecation")
  @Override
  public List<String> getResourceFormats(int limit)
  {
    final String method = "getResourceFormats() : ";
    log.trace(method + "Start");

    // Prepare Aggregation
    TermsAggregationBuilder aggFormat = AggregationBuilders
        .terms(SearchConsts.FACET_FORMAT)
        .field(ESFieldConsts.FIELD_METADATA_RESOURCES_FORMAT)
        .size(limit);

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.aggregation(aggFormat);

    // Execute Request
    SearchRequest searchRequest = new SearchRequest().indices(indexNames).source(sourceBuilder);

    List<String> formats = new ArrayList<>();
    SearchResponse actionGet = null;
    try
    {
      actionGet = client.search(searchRequest, RequestOptions.DEFAULT);
    }
    catch (IOException ex)
    {
      log.warn("{}Elasticsearch-request failed: {}", method, ex.getMessage());
      return formats;
    }
    Terms terms = actionGet.getAggregations().get(SearchConsts.FACET_FORMAT);

    // Build List
    for (Bucket b : terms.getBuckets())
    {
      formats.add(b.getKeyAsString().toString());
    }

    log.trace(method + "End");
    return formats;
  }

  /**
   * Stores the given phrase in the Elastic Search search history index.
   *
   * @param phrase
   */
  @SuppressWarnings("deprecation")
  @Override
  public void recordSearchPhrase(final String phrase)
  {
    final String method = "recordSearchPhrase() : ";
    log.trace(method + "Start");

    if (StringUtils.length(StringUtils.trim(phrase)) < SEARCH_PHRASE_MIN_LENGTH)
    {
      return;
    }

    IndexRequest indexRequest = new IndexRequest(searchhistoryIndexName);
    indexRequest.source(FIELD_HISTORYITEM_PHRASE, phrase, FIELD_TIMESTAMP, new Date());

    try
    {
      client.index(indexRequest, RequestOptions.DEFAULT);
    }
    catch (IOException ex)
    {
      log.warn("{}Elasticsearch-request failed: {}", method, ex.getMessage());
    }
    log.trace(method + "End");
  }

  @Override
  @SuppressWarnings("deprecation")
  public SearchHits getMetrics()
  {
    final String method = "getMetrics() : ";
    log.trace(method + "Start");

    // empty search hits
    SearchHits result = SearchHits.empty();

    // get the latest available date
    String latestDate = getMetricsLatestDate();
    if (StringUtils.isBlank(latestDate))
    {
      log.info("{}Did not find any metrics data.", method);
      return result;
    }
    MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("date", latestDate);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
        .query(matchQueryBuilder)
        .size(100);

    // send search request
    SearchRequest searchRequest = new SearchRequest().indices(metricIndexName).source(sourceBuilder)
        .searchType(SearchType.DFS_QUERY_THEN_FETCH)
        .scroll(new Scroll(TimeValue.timeValueMinutes(minutesKeepAliveScroll)));

    try
    {
      SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
      List<SearchHit> combinedSearchHits = new ArrayList<>();

      SearchHit[] hits = response.getHits().getHits();

      while (hits.length != 0)
      {
        for (SearchHit searchHit : hits)
        {
          combinedSearchHits.add(searchHit);
        }

        response = client.searchScroll(new SearchScrollRequest(response.getScrollId())
            .scroll(new Scroll(TimeValue.timeValueMinutes(minutesKeepAliveScroll))), RequestOptions.DEFAULT);
        hits = response.getHits().getHits();
      }

      hits = combinedSearchHits.toArray(new SearchHit[0]);
      result = new SearchHits(hits, response.getHits().getTotalHits(), 1);
    }
    catch (IOException ex)
    {
      log.warn("{}Elasticsearch-request failed: {}", method, ex.getMessage());
    }
    log.trace(method + "End");
    return result;
  }

  @Override
  public SearchResultContainer getNumbers()
  {
    // prepare query
    final QueryBuilder baseQuery = QueryBuilders.matchAllQuery();
    BoolQueryBuilder filters = QueryBuilders.boolQuery();

    // filter private datasets/showcases
    filters.filter(getPrivateFilter());

    // filter for types
    Map<String, FilterListDto> filterMap = new HashMap<>();
    // get a count for each type
    filterMap.put(SearchConsts.FILTER_KEY_TYPE, buildTypeFacet(baseQuery, filters));

    SearchResultContainer result = SearchResultContainer.builder()
        .filterMap(filterMap)
        .build();
    return result;
  }

  // private methods

  protected List<HitDto> mapSearchResult(SearchResponse response)
  {
    List<HitDto> mappedHits = new ArrayList<>();
    for (SearchHit searchHit : response.getHits().getHits())
    {
      HitDto indexEntry = searchHitMapper.mapToHitDto(searchHit);
      mappedHits.add(indexEntry);
    }
    return mappedHits;
  }

  /**
   * Metrics private function
   * Returns the value of the latest available date
   * @return
   */
  @SuppressWarnings("deprecation")
  private String getMetricsLatestDate()
  {
    final String method = "getMetricsLatestDate() : ";
    log.trace(method + "Start");

    String latestDate = "";

    MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1);
    sourceBuilder.query(matchAllQuery);
    sourceBuilder.sort(ESFieldConsts.FIELD_METRICS_DATE, SortOrder.DESC);

    // send search request
    SearchRequest searchRequest = new SearchRequest().indices(metricIndexName).source(sourceBuilder);

    try
    {
      SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
      Optional<SearchHit> opt = Stream.of(response.getHits().getHits()).findFirst();
      if (opt.isPresent())
      {
        latestDate = (String) opt.get().getSourceAsMap().get(ESFieldConsts.FIELD_METRICS_DATE);
        log.info("{}Get value for metrics latest date: {}", method, latestDate);
      }
    }
    catch (IOException ex)
    {
      log.warn("{}Elasticsearch-request failed: {}", method, ex.getMessage());
    }

    log.trace(method + "End");
    return latestDate;
  }

  /**
   * Returns a Filter Builder for ignoring private datasets/showcases in the search.
   * @return
   */
  private BoolQueryBuilder getPrivateFilter()
  {
    BoolQueryBuilder privateFilter =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(ESFieldConsts.PRIVATE, false));
    // adding all liferay-data, which don't have that field.
    BoolQueryBuilder missingFilter =
        QueryBuilders.boolQuery().mustNot(new ExistsQueryBuilder(ESFieldConsts.PRIVATE));

    return QueryBuilders.boolQuery().should(privateFilter).should(missingFilter);
  }

  /**
   * Builder for IsOpen Facet - only 2 possible fixed options
   *
   * @param agsResult
   * @return
   */
  private FilterListDto buildIsOpenFacet(Aggregations agsResult)
  {
    FilterListDto aggIsOpenResult = new FilterListDto();

    long openDatasets = ((Filter) agsResult.get(SearchConsts.FACET_HAS_OPEN)).getDocCount();
    aggIsOpenResult.add(buildFacetDto(SearchConsts.FACET_HAS_OPEN, openDatasets));

    long restricedDatasets = ((Filter) agsResult.get(SearchConsts.FACET_HAS_CLOSED)).getDocCount();
    aggIsOpenResult.add(buildFacetDto(SearchConsts.FACET_HAS_CLOSED, restricedDatasets));

    return aggIsOpenResult;
  }

  /**
   * Builder for buildBoolFilterFacet Facet - only 1 possible fixed option
   *
   * @param agsResult
   * @param facet
   * @return
   */
  private FilterListDto buildBoolFilterFacet(Aggregations agsResult, String facet)
  {
    FilterListDto agg = new FilterListDto();
    long datasetsCount = ((Filter) agsResult.get(facet)).getDocCount();
    agg.add(buildFacetDto(facet, datasetsCount));
    return agg;
  }

  private QueryBuilder createTypeFilter(String typeFilterString)
  {
    QueryBuilder typeFilter = QueryBuilders.termsQuery(ESFieldConsts.FIELD_METADATA_TYPE,
        filterUtil.getDefaultTypeFilterValues());
    {
      if (StringUtils.isNotBlank(typeFilterString))
      {
        typeFilter = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery(ESFieldConsts.FIELD_METADATA_TYPE, typeFilterString));
      }
    }
    return typeFilter;
  }

  private FilterListDto buildTypeFacet(final QueryBuilder baseQuery, BoolQueryBuilder filters)
  {
    FilterListDto typeFilterList = new FilterListDto();
    typeFilterList.setSingletonFiltergroup(true); // there can be only one type filter

    for (String filterTypeValue : SearchConsts.VALID_FILTER_TYPES_WITHOUT_ALL_ORDERED)
    {
      if (filterUtil.getDefaultTypeFilterValues().contains(filterTypeValue))
      {
        typeFilterList.add(buildFacetDto(filterTypeValue, baseQuery, filters));
      }
    }

    return typeFilterList;
  }

  private FilterListDto buildTypeFacet(
      final QueryBuilder baseQuery, BoolQueryBuilder filters, long totalHitsAllTypes)
  {
    FilterListDto typeFilterList = buildTypeFacet(baseQuery, filters);
    typeFilterList.add(0, buildFacetDto(SearchConsts.TYPE_ALL, totalHitsAllTypes));
    return typeFilterList;
  }

  /**
   * Build Aggregations for all data-based facettes on the filter panel.
   */
  private List<AggregationBuilder> buildAggregations()
  {
    List<AggregationBuilder> aggregations = new ArrayList<>();
    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_GROUPS)
        .field(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_GROUPS))
        .size(1000)
        .order(BucketOrder.key(true)));

    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_TAGS)
        .field(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_TAGS)));

    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_FORMAT)
        .field(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_FORMAT)));

    aggregations.add(AggregationBuilders
        .filter(SearchConsts.FACET_HAS_OPEN, QueryBuilders.boolQuery().must(
            QueryBuilders.termQuery(ESFieldConsts.BOOL_FACET_MAP.get(SearchConsts.FACET_HAS_OPEN), "true"))));

    aggregations.add(AggregationBuilders
        .filter(SearchConsts.FACET_HAS_CLOSED, QueryBuilders.boolQuery().must(
            QueryBuilders.termQuery(ESFieldConsts.BOOL_FACET_MAP.get(SearchConsts.FACET_HAS_CLOSED),
                "true"))));

    aggregations.add(AggregationBuilders
        .filter(SearchConsts.FACET_HAS_DATA_SERVICE, QueryBuilders.boolQuery().must(
            QueryBuilders.termQuery(ESFieldConsts.BOOL_FACET_MAP.get(SearchConsts.FACET_HAS_DATA_SERVICE),
                "true"))));

    if (ArrayUtils.isNotEmpty(highValueDatasetTags))
    {
      aggregations.add(AggregationBuilders.filter(SearchConsts.FACET_IS_HIGH_VALUE_DATASET,
          QueryBuilders.termsQuery(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_TAGS),
              highValueDatasetTags)));
    }

    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_LICENCE)
        .field(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_LICENCE)));

    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_SHOWCASE_TYPE)
        .script(new Script(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_SHOWCASE_TYPE))));

    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_PLATFORMS)
        .field(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_PLATFORMS)));

    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_SOURCEPORTAL)
        .field(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_SOURCEPORTAL))
        .size(1000));

    return aggregations;
  }

  /**
   * Queries Elastic Search for suggestions regarding the given search phrase. One ES-Call is made
   * for each index. This way we still get results, even if one index does return an empty list.
   * (Querying all indices at once does return an empty list if one index has no suggestions.)
   *
   * @param q current phrase which needs suggestionsilter
   * @return List of suggested search phrases
   */
  @SuppressWarnings("deprecation")
  private Set<SuggestionOption> querySuggestions(String q)
  {
    final String method = "querySuggestions() : ";
    log.trace(method + "Start");

    Set<SuggestionOption> suggestionOptions = new HashSet<>();

    DirectCandidateGeneratorBuilder directCandidateGenerator =
        new DirectCandidateGeneratorBuilder("title").suggestMode("always");

    PhraseSuggestionBuilder phraseSuggestionBuilder = new PhraseSuggestionBuilder("title")
        .text(q)
        .size(5)
        .realWordErrorLikelihood(.5f)
        .maxErrors(.5f)
        .gramSize(2)
        .addCandidateGenerator(directCandidateGenerator);

    for (String index : indexNames)
    {
      SuggestBuilder suggestBuilder = new SuggestBuilder();
      suggestBuilder.addSuggestion(SUGGEST_DID_YOU_MEAN, phraseSuggestionBuilder);

      SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
      sourceBuilder.suggest(suggestBuilder);
      SearchRequest searchRequest = new SearchRequest().indices(index).source(sourceBuilder);
      SearchResponse response;
      try
      {
        response = client.search(searchRequest, RequestOptions.DEFAULT);
      }
      catch (IOException ex)
      {
        log.warn("{}Elasticsearch-request failed: {}", method, ex.getMessage());
        return suggestionOptions;
      }
      Suggest suggest = response.getSuggest();
      if (suggest != null)
      {
        Suggestion<? extends Entry<? extends Option>> suggestion =
            suggest.getSuggestion(SUGGEST_DID_YOU_MEAN);
        if (suggestion != null)
        {
          for (Entry<? extends Option> entry : suggestion)
          {
            for (Option option : entry.getOptions())
            {
              suggestionOptions.add(new SuggestionOption(option.getText().string(), option.getScore()));
            }
          }
        }
      }
    }
    log.trace(method + "End");
    return suggestionOptions;
  }

  /**
   * Creates the basic Search Query
   *
   * @param q Search Phrase
   * @return Basic Query to be used as part of the request to elastic search
   */
  private QueryBuilder buildBaseQuery(SearchQuery q)
  {
    final QueryBuilder baseQuery;
    if (Objects.isNull(q) || q.isEmpty())
    {
      baseQuery = QueryBuilders.matchAllQuery();
    }
    else
    {
      String queryString = q.getQueryString();
      baseQuery = QueryBuilders
          .boolQuery()
          .should(
              createMultiMatchBaseQuery(
                  queryString,
                  1,
                  "title.ngram",
                  "preamble.ngram",
                  "sections.headline.ngram",
                  "sections.text.ngram"))
          .should(
              createMultiMatchBaseQuery(
                  queryString,
                  2,
                  "title.edge_ngram",
                  "preamble.edge_ngram",
                  "sections.headline.edge_ngram",
                  "sections.text.edge_ngram"))
          .should(
              createMultiMatchBaseQuery(
                  queryString,
                  5,
                  ESFieldConsts.FIELD_TITLE,
                  ESFieldConsts.FIELD_DESCRIPTION,
                  "sections.headline",
                  "sections.text",
                  "metadata.author",
                  ESFieldConsts.FIELD_METADATA_MAINTAINER,
                  ESFieldConsts.FIELD_METADATA_PUBLISHER_NAME))
          .should(
              createMultiMatchBaseQuery(
                  queryString,
                  5,
                  ESFieldConsts.FIELD_TAGS_SEARCH));
    }
    return baseQuery;
  }

  /**
   * Creates a filter for the state-search.
   * @param stateFilters
   * @return
   */
  private QueryBuilder buildStateSearchQuery(Optional<BaseFilter> stateFilters)
  {
    if (stateFilters.isPresent())
    {
      return QueryBuilders.boolQuery().must(stateFilters.get().createFilter());
    }
    // return empty query
    return QueryBuilders.boolQuery();
  }

  /**
   * Iterates the List of Filters and removes all groups which are not part of the official list.
   *
   * @param groupAggregateList
   */
  private void stripUnofficialGroups(FilterListDto groupAggregateList)
  {
    Map<String, Category> categoryMap = categoryCache.getCategoryMap();

    groupAggregateList.removeIf(filter -> !categoryMap.containsKey(filter.getName()));
  }

  /**
   * Maps the sort to elastic search
   *
   * @param sort
   * @param forceRelevanceSort
   * @return
   */
  private SortBuilder<?> createSorting(Sort sort, SearchQuery q, boolean forceRelevanceSort)
  {
    SortOrder order = SortOrder.DESC;
    if (sort.getAscending())
    {
      order = SortOrder.ASC;
    }

    switch (sort.getType())
    {
      case LASTMODIFICATION:
        return SortBuilders.fieldSort(ESFieldConsts.SORT_DATE).order(order);

      case TITLE:
        return SortBuilders.fieldSort(ESFieldConsts.SORT_TITLE).order(order);

      case RELEVANCE:
      default:
        // Default sort if no search phrase is given
        if (!forceRelevanceSort && (Objects.isNull(q) || q.isEmpty()))
        {
          return SortBuilders.fieldSort(ESFieldConsts.SORT_DATE).order(order);
        }
        return SortBuilders.scoreSort().order(order);
    }
  }

  /**
   * Maps the Aggregation from ElasticSearch results to a List of FilterDtos
   *
   * @param key name of the Aggregation
   * @param agg Aggregation to map
   * @return
   */
  private FilterListDto buildListFacet(String key, Terms agg)
  {
    FilterListDto aggList = new FilterListDto();
    for (Terms.Bucket entry : agg.getBuckets())
    {
      aggList.add(buildFacetDto(entry.getKeyAsString(), entry.getDocCount()));
    }
    return aggList;
  }

  private FacetDto buildFacetDto(String type, QueryBuilder baseQuery, BoolQueryBuilder filters)
  {
    QueryBuilder typeFilter =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(ESFieldConsts.FIELD_METADATA_TYPE, type));
    return buildFacetDto(type, countHits(baseQuery, typeFilter, filters));
  }

  private FacetDto buildFacetDto(String name, long docCount)
  {
    return FacetDto.builder().name(name).docCount(docCount).build();
  }

  @SuppressWarnings("deprecation")
  private long countHits(
      QueryBuilder baseQuery,
      QueryBuilder typeFilter,
      BoolQueryBuilder filters)
  {
    final String method = "countHits() : ";
    log.trace(method + "Start");

    BoolQueryBuilder allFilters = QueryBuilders.boolQuery().must(filters).must(typeFilter);

    final QueryBuilder filteredQuery = QueryBuilders.boolQuery().filter(baseQuery).filter(allFilters);

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(filteredQuery);

    CountRequest countRequest = new CountRequest().indices(indexNames).source(sourceBuilder);

    try
    {
      CountResponse response = client.count(countRequest, RequestOptions.DEFAULT);
      log.trace(method + "End");
      return response.getCount();
    }
    catch (IOException ex)
    {
      log.warn("{}Elasticsearch-request failed: {}", method, ex.getMessage());
    }
    log.trace(method + "End");
    return 0L;
  }

  private MultiMatchQueryBuilder createMultiMatchBaseQuery(String q, float boost, String... fieldNames)
  {
    return QueryBuilders
        .multiMatchQuery(
            q,
            fieldNames)
        .type(Type.MOST_FIELDS)
        .boost(boost)
        .operator(Operator.AND);
  }

  // Getter / Setter
  /**
   * Returns a copy of the current nodes array if not null.
   * 
   * @return
   */
  public String[] getNodes()
  {
    if (Objects.nonNull(nodes))
    {
      return nodes.clone();
    }
    return null;
  }

  /**
   * Set the elasticsearch nodes.
   * 
   * @param nodes the nodes as comma separated list
   */
  @Value("${elasticsearch.nodes}")
  public void setNodes(String nodes)
  {
    String[] nodeArray = StringUtils.stripAll(StringUtils.splitByWholeSeparator(nodes, ","));
    this.nodes = nodeArray;
  }

  /**
   * Returns a copy of the current index names array if not null.
   * 
   * @return
   */
  public String[] getIndexNames()
  {
    if (Objects.nonNull(indexNames))
    {
      return indexNames.clone();
    }
    return null;
  }

  /**
   * Setzt die Indexnamen, die für die Suche genutzt werden. Die Indexnamen werden als
   * komma-separierte Liste erwartet.
   *
   * @param indexNames die Indexnamen, z.B. "index1,index2".
   */
  @Value(SearchConsts.CONFIG_ELASTICSEARCH_SEARCH_INDEX_NAMES)
  public void setIndexNamesFromString(String indexNames)
  {
    String[] indexNameArray = StringUtils.stripAll(StringUtils.splitByWholeSeparator(indexNames, ","));
    this.indexNames = indexNameArray;
  }

  public int getMinutesKeepAliveScroll()
  {
    return minutesKeepAliveScroll;
  }

  @Value("${elasticsearch.search.scroll.keepAlive.minutes:5}")
  public void setMinutesKeepAliveScroll(int minutesKeepAliveScroll)
  {
    this.minutesKeepAliveScroll = minutesKeepAliveScroll;
  }

  /**
   * Setzt welche Tags einen Datensatz als High-Value-Dataset beschreiben.
   *
   * @param highValueDatasetTags die Tags, z.B. "hvd,highvaluedataset".
   */
  @Value("${elasticsearch.high.value.dataset.tags}")
  public void setHighValueDatasetTags(String highValueDatasetTags)
  {
    this.highValueDatasetTags =
        StringUtils.stripAll(StringUtils.splitByWholeSeparator(highValueDatasetTags, ","));
  }
}
