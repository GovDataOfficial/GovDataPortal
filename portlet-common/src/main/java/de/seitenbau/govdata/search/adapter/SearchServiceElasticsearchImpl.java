package de.seitenbau.govdata.search.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.ConfigurationException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.MissingFilterBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.index.query.TypeFilterBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.Suggest.Suggestion;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder.DirectCandidateGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.filter.SearchConsts;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.search.common.ESFieldConsts;
import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.filter.BaseFilter;
import de.seitenbau.govdata.search.index.PortalIndexConstants;
import de.seitenbau.govdata.search.index.mapper.SearchHitMapper;
import de.seitenbau.govdata.search.index.model.FacetDto;
import de.seitenbau.govdata.search.index.model.FacetDto.FacetDtoBuilder;
import de.seitenbau.govdata.search.index.model.FilterListDto;
import de.seitenbau.govdata.search.index.model.HitDto;
import de.seitenbau.govdata.search.index.model.SearchResultContainer;
import de.seitenbau.govdata.search.index.model.SuggestionOption;
import de.seitenbau.govdata.search.sort.Sort;
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

  private static final String TYPE_HISTORYITEM = "historyitem";

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
   * Maximum distance between boundingbox-center and a datasets spatial center.
   * This is approx. half of the maximal distance two points in Germany can have
   * (The ranked dataset has to be inside the boundingbox. So assuming the boundingbox
   * is only be as big as whole germany, the center is at most half of Germanys size away
   * from the dataset's center.)
   */
  private static final String MAX_RADIUS_GERMANY = "450km";

  private TransportClient client;

  private String cluster;

  private String[] nodes;

  private String[] indexNames;

  private int minutesKeepAliveScroll;

  private String[] defaultTypeFilterValues;

  @Autowired
  private SearchHitMapper searchHitMapper;

  @Autowired
  private CategoryCache categoryCache;

  @Value("${elasticsearch.search.index.searchhistory}")
  private String searchhistoryIndexName;

  @Value("${elasticsearch.search.spatial_area_boost}")
  private float spatialAreaBoost;

  @Value("${elasticsearch.search.spatial_center_boost}")
  private float spatialCenterBoost;

  /**
   * Initialisiert die benötigten Objekte.
   *
   * @throws ConfigurationException bei einem Fehler in der Konfiguration.
   */
  @PostConstruct
  public void init() throws ConfigurationException
  {
    Settings settings = ImmutableSettings.settingsBuilder()
        .put("cluster.name", cluster).build();
    client = new TransportClient(settings);

    for (String host : nodes)
    {
      String[] hostAndPort = host.trim().split(":");
      if (hostAndPort.length != 2)
      {
        String msg = "Invalid host configuration '" + host + "', expected [hostname]:[port]";
        log.error(msg);
        throw new ConfigurationException(msg);
      }
      client.addTransportAddress(
          new InetSocketTransportAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
    }
  }

  /**
   * Räumt die initialisierten Objekte wieder auf, zum Beispiel schließen von offenen Verbindungen.
   */
  @PreDestroy
  public void destroy()
  {
    client.close();
  }

  /**
   * Führt eine Suche mit den angegebenen Suchbegriffen und Filtern aus und gibt das Ergebnis
   * zurück.
   *
   * @param q          der Suchbegriff.
   * @param numResults die Anzahl der maximalen Treffer, die zurückgeliefert werden sollen. Falls
   *                   null übergeben wird das Default von {@value #DEFAULT_SEARCH_RESULT_SIZE} genutzt.
   * @param bundle     die anzuwendenden Filter.
   * @param sort       die Sortierung.
   * @return die Treffer mit zusätzlichen Informationen wie Facetten.
   * @see de.seitenbau.govdata.search.adapter.SearchService#search(java.lang.String,
   * java.lang.Integer, de.seitenbau.govdata.search.common.SearchFilterBundle,
   * de.seitenbau.govdata.search.sort.Sort)
   */
  @Override
  public SearchResultContainer search(
      String q, Integer numResults, SearchFilterBundle bundle, Sort sort)
  {
    final BaseQueryBuilder baseQuery = buildBaseQuery(q);

    // Map of active Filters, used in result presentation
    Map<String, FilterListDto> activeFilterMap = new HashMap<>();

    // will contain all filters except for document types (is handled separately)
    AndFilterBuilder filters = new AndFilterBuilder();

    // Create all supplied filters from their transport objects
    for (BaseFilter filter : bundle.getFilters())
    {
      filters.add(filter.createFilter());
    }

    // filter on certain types
    FilterBuilder typeFilter = createTypeFilter(bundle.getTypeFilter());

    // filter private datasets (deleted / not published)
    if (bundle.getHidePrivateDatasets())
    {
      // filtering ckan-data
      TermFilterBuilder privateFilter = FilterBuilders.termFilter(ESFieldConsts.PRIVATE, false);

      // adding all liferay-data, which don't have that field.
      MissingFilterBuilder missingFilter = FilterBuilders.missingFilter(ESFieldConsts.PRIVATE);

      filters.add(FilterBuilders.orFilter(privateFilter, missingFilter));
    }

    // aggregate statistics for filter-facettes
    List<AbstractAggregationBuilder> aggregations = buildAggregations();

    // combine filters (adding AND to AND seems stupid, but we need "filters" unmodified later)
    AndFilterBuilder combinedFilters = new AndFilterBuilder(filters);
    if (typeFilter != null)
    {
      combinedFilters.add(typeFilter);
    }

    final FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(baseQuery, combinedFilters);

    // Prepare Search
    int numberOfResults = DEFAULT_SEARCH_RESULT_SIZE;
    if (numResults != null)
    {
      numberOfResults = numResults;
    }
    SearchRequestBuilder search = client.prepareSearch(indexNames)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setSize(numberOfResults).setFrom(0)
        .setExplain(false)
        // set scroll for ongoing search by pagination
        .setScroll(new Scroll(TimeValue.timeValueMinutes(minutesKeepAliveScroll)));

    // Add Query
    if (bundle.getBoostSpatialRelevance() && (spatialAreaBoost > 0 || spatialCenterBoost > 0))
    {
      // if boostSpatialRelevance is active (and weights are enabled), wrap the query in a function
      // score query
      FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders
          .functionScoreQuery(filteredQuery)
          .add(ScoreFunctionBuilders
              .linearDecayFunction("metadata.spatial_area", 0, AREA_GERMANY)
              .setWeight(spatialAreaBoost))
          .add(ScoreFunctionBuilders
              .linearDecayFunction("metadata.spatial_center", bundle.getSpatialCenter(), MAX_RADIUS_GERMANY)
              .setWeight(spatialCenterBoost))
          .boostMode("sum");

      search.setQuery(functionScoreQuery);
    }
    else
    {
      // nothing special, use the filteredQuery as usual
      search.setQuery(filteredQuery);
    }

    // Add Aggregations
    for (AbstractAggregationBuilder ags : aggregations)
    {
      search.addAggregation(ags);
    }

    // Add Sorting
    if (sort != null && sort.getType() != null && sort.getAscending() != null)
    {
      search.addSort(createSorting(sort));
    }

    //System.out.println(search.toString());

    // execute search
    SearchResponse response = search.execute().actionGet();
    //System.out.println(response.toString());

    // Search and count hits per type
    SearchHits responseSearchHits = response.getHits();
    long responseSearchHitsCount = ArrayUtils.getLength(responseSearchHits.getHits());
    long totalHits = responseSearchHits.getTotalHits();
    long totalHitsAllTypes = totalHits;
    if (typeFilter != null)
    {
      totalHitsAllTypes =
          countHits(baseQuery,
              FilterBuilders.inFilter(ESFieldConsts.FIELD_METADATA_TYPE, defaultTypeFilterValues),
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

    activeFilterMap.put(SearchConsts.FACET_OPENNESS, buildIsOpenFacet(agsResult));

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
    if (totalHits == 0 && StringUtils.isNotEmpty(q))
    {
      suggestionOptions = querySuggestions(q);
    }

    SearchResultContainer result = SearchResultContainer.builder()
        .moreNextHitsAvailable(totalHits > responseSearchHitsCount)
        .pageSize(numberOfResults)
        .hits(mapSearchResult(response))
        .filterMap(activeFilterMap)
        .scrollId(response.getScrollId())
        .suggestions(suggestionOptions)
        .build();

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

  @Override
  public SearchResultContainer scrollNextHits(String scrollId)
  {
    SearchResponse searchResponse;
    try
    {
      searchResponse = client
          .prepareSearchScroll(scrollId)
          .setScroll(new Scroll(TimeValue.timeValueMinutes(minutesKeepAliveScroll)))
          .execute()
          .actionGet();
    }
    catch (ElasticsearchException e)
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

  @Override
  public List<String> findPortalContentIdsByPortletId(String portletId)
  {
    MatchAllQueryBuilder baseQuery = QueryBuilders.matchAllQuery();

    TypeFilterBuilder typeFilter = FilterBuilders.typeFilter(PortalIndexConstants.INDEX_TYPE_PORTAL);
    TermsFilterBuilder portletIdFilter = FilterBuilders.termsFilter("metadata.portletId", portletId);
    AndFilterBuilder finalFilter = FilterBuilders.andFilter(typeFilter, portletIdFilter);

    final FilteredQueryBuilder filteredQuery = QueryBuilders
        .filteredQuery(
            baseQuery,
            finalFilter);

    SearchRequestBuilder search = client.prepareSearch(indexNames)
        .setSearchType(SearchType.SCAN)
        .setScroll(TimeValue.timeValueMinutes(minutesKeepAliveScroll))
        .setSize(100)
        .setQuery(filteredQuery)
        .setExplain(false);

    SearchResponse response = search.execute().actionGet();

    List<String> contentIds = new ArrayList<>();

    response =
        client.prepareSearchScroll(response.getScrollId())
            .setScroll(TimeValue.timeValueMinutes(minutesKeepAliveScroll))
            .execute()
            .actionGet();
    while (response.getHits().getHits().length != 0)
    {
      for (SearchHit searchHit : response.getHits().getHits())
      {
        HitDto indexEntry = searchHitMapper.mapToHitDto(searchHit);
        contentIds.add(indexEntry.getId());
      }
      response =
          client.prepareSearchScroll(response.getScrollId())
              .setScroll(TimeValue.timeValueMinutes(minutesKeepAliveScroll))
              .execute()
              .actionGet();
    }

    return contentIds;
  }

  @Override
  public List<String> findSearchAsYouTypeSuggestions(String q)
  {
    List<String> suggestions = new ArrayList<>();

    CompletionSuggestionBuilder suggestionBuilder =
        new CompletionSuggestionBuilder(SAYT_COMPLETION_SUGGESTION);
    suggestionBuilder.text(q);
    suggestionBuilder.field("title_suggest");

    SuggestResponse suggestResponse =
        client.prepareSuggest(indexNames).addSuggestion(suggestionBuilder).execute().actionGet();
    Suggest suggest = suggestResponse.getSuggest();
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
            suggestions.add(option.getText().string());
          }
        }
      }
    }

    return suggestions;
  }

  @Override
  public List<String> getResourceFormats(int limit)
  {
    // Prepare Aggregation
    TermsBuilder aggFormat = AggregationBuilders
        .terms(SearchConsts.FACET_FORMAT)
        .field(ESFieldConsts.FIELD_METADATA_RESOURCES_FORMAT)
        .size(limit);

    // Execute Request
    SearchResponse actionGet =
        client.prepareSearch(indexNames).addAggregation(aggFormat).execute().actionGet();
    Terms terms = actionGet.getAggregations().get(SearchConsts.FACET_FORMAT);

    // Build List
    List<String> formats = new ArrayList<>();
    for (Bucket b : terms.getBuckets())
    {
      formats.add(b.getKeyAsText().toString());
    }

    return formats;
  }

  /**
   * Stores the given phrase in the Elastic Search search history index.
   *
   * @param phrase
   */
  @Override
  public void recordSearchPhrase(final String phrase)
  {
    if (StringUtils.length(StringUtils.trim(phrase)) < SEARCH_PHRASE_MIN_LENGTH)
    {
      return;
    }

    client.prepareIndex(searchhistoryIndexName, TYPE_HISTORYITEM)
        .setSource(FIELD_HISTORYITEM_PHRASE, phrase, FIELD_TIMESTAMP, new Date())
        .execute();
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

  private FilterBuilder createTypeFilter(String typeFilterString)
  {
    FilterBuilder typeFilter = null;
    {
      if (StringUtils.isNotBlank(typeFilterString))
      {
        typeFilter = FilterBuilders.termFilter(ESFieldConsts.FIELD_METADATA_TYPE, typeFilterString);
      }
    }
    return typeFilter;
  }

  private FilterListDto buildTypeFacet(
      final BaseQueryBuilder baseQuery, AndFilterBuilder filters, long totalHitsAllTypes)
  {
    FilterListDto typeFilterList = new FilterListDto();
    typeFilterList.setSingletonFiltergroup(true); // there can be only one type filter
    typeFilterList.add(buildFacetDto(SearchConsts.TYPE_ALL, totalHitsAllTypes));
    typeFilterList.add(
        buildFacetDto(
            new String[] {SearchConsts.TYPE_DATASET}, baseQuery, filters));
    typeFilterList.add(buildFacetDto(SearchConsts.TYPE_ARTICLE, baseQuery, filters));
    typeFilterList.add(buildFacetDto(SearchConsts.TYPE_BLOG, baseQuery, filters));
    return typeFilterList;
  }

  /**
   * Build Aggregations for all data-based facettes on the filter panel.
   */
  private List<AbstractAggregationBuilder> buildAggregations()
  {
    List<AbstractAggregationBuilder> aggregations = new ArrayList<>();
    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_GROUPS)
        .field(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_GROUPS))
        .size(0)
        .order(Terms.Order.term(true)));

    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_TAGS)
        .field(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_TAGS)));

    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_FORMAT)
        .field(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_FORMAT)));

    aggregations.add(AggregationBuilders
        .filter(SearchConsts.FACET_HAS_OPEN)
        .filter(FilterBuilders
            .termFilter(ESFieldConsts.BOOL_FACET_MAP.get(SearchConsts.FACET_HAS_OPEN), "true")));

    aggregations.add(AggregationBuilders
        .filter(SearchConsts.FACET_HAS_CLOSED)
        .filter(FilterBuilders
            .termFilter(ESFieldConsts.BOOL_FACET_MAP.get(SearchConsts.FACET_HAS_CLOSED), "true")));

    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_LICENCE)
        .field(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_LICENCE)));

    aggregations.add(AggregationBuilders
        .terms(SearchConsts.FACET_SOURCEPORTAL)
        .field(ESFieldConsts.FACET_MAP.get(SearchConsts.FACET_SOURCEPORTAL))
        .size(0));

    return aggregations;
  }

  /**
   * Queries Elastic Search for suggestions regarding the given search phrase.
   * One ES-Call is made for each index. This way we still get results, even
   * if one index does return an empty list.
   * (Querying all indices at once does return an empty list if one index has
   * no suggestions.)
   *
   * @param q current phrase which needs suggestions
   * @return List of suggested search phrases
   */
  private Set<SuggestionOption> querySuggestions(String q)
  {
    Set<SuggestionOption> suggestionOptions = new HashSet<>();

    DirectCandidateGenerator directCandidateGenerator = new PhraseSuggestionBuilder
        .DirectCandidateGenerator("title")
        .suggestMode("always");

    PhraseSuggestionBuilder phraseSuggestionBuilder = new PhraseSuggestionBuilder(SUGGEST_DID_YOU_MEAN)
        .text(q)
        .field("title")
        .size(5)
        .realWordErrorLikelihood(.5f)
        .maxErrors(.5f)
        .gramSize(2)
        .addCandidateGenerator(directCandidateGenerator);

    for (String index : indexNames)
    {
      SuggestResponse suggestResponse =
          client.prepareSuggest(index).addSuggestion(phraseSuggestionBuilder).execute().actionGet();
      Suggest suggest = suggestResponse.getSuggest();
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

    return suggestionOptions;
  }

  /**
   * Creates the basic Search Query
   *
   * @param q Search Phrase
   * @return Basic Query to be used as part of the request to elastic search
   */
  private BaseQueryBuilder buildBaseQuery(String q)
  {
    final BaseQueryBuilder baseQuery;
    if (null == q || q.isEmpty() || q.equals("*"))
    {
      baseQuery = QueryBuilders.matchAllQuery();
    }
    else
    {
      baseQuery = QueryBuilders
          .boolQuery()
          .should(
              createMultiMatchBaseQuery(
                  q,
                  1,
                  "title.ngram",
                  "preamble.ngram",
                  "sections.headline.ngram",
                  "sections.text.ngram"))
          .should(
              createMultiMatchBaseQuery(
                  q,
                  2,
                  "title.edge_ngram",
                  "preamble.edge_ngram",
                  "sections.headline.edge_ngram",
                  "sections.text.edge_ngram"))
          .should(
              createMultiMatchBaseQuery(
                  q,
                  5,
                  "title",
                  "preamble",
                  "sections.headline",
                  "sections.text",
                  "metadata.author",
                  "metadata.maintainer"))
          .should(
              createMultiMatchBaseQuery(
                  q,
                  5,
                  ESFieldConsts.FIELD_TAGS_SEARCH));
    }
    return baseQuery;
  }

  /**
   * Iterates the List of Filters and removes all groups
   * which are not part of the official list.
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
   * @return
   */
  private SortBuilder createSorting(Sort sort)
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
      aggList.add(buildFacetDto(entry.getKey(), entry.getDocCount()));
    }
    return aggList;
  }

  private FacetDto buildFacetDto(String type, BaseQueryBuilder baseQuery, FilterBuilder filters)
  {
    TermsFilterBuilder typeFilter = FilterBuilders.termsFilter(ESFieldConsts.FIELD_METADATA_TYPE, type);
    return buildFacetDto(type, countHits(baseQuery, typeFilter, filters));
  }

  /**
   * Erzeugt ein {@link FacetDto}-Objekt aus den übergebenen Parametern. Es werden alle Treffer zu
   * den übergebenen Typen (<code>typeArray</code>) ermittelt, ein Filter mit dem Namen des ersten
   * Typs erstellt und zurückgegeben.
   *
   * @param typeArray das Array mit den Typen.
   * @param baseQuery die Base-Query.
   * @return der erstellte Filter.
   */
  private FacetDto buildFacetDto(String[] typeArray, BaseQueryBuilder baseQuery, FilterBuilder filters)
  {
    FacetDtoBuilder builder = FacetDto.builder();
    if (ArrayUtils.isNotEmpty(typeArray))
    {
      long hitsCount = 0;
      String name = typeArray[0];
      // counting hits
      for (String type : typeArray)
      {
        TermsFilterBuilder typeFilter = FilterBuilders.termsFilter(ESFieldConsts.FIELD_METADATA_TYPE, type);
        hitsCount += countHits(baseQuery, typeFilter, filters);
      }
      builder.name(name).docCount(hitsCount);
    }
    return builder.build();
  }

  private FacetDto buildFacetDto(String name, long docCount)
  {
    return FacetDto.builder().name(name).docCount(docCount).build();
  }

  private long countHits(
      BaseQueryBuilder baseQuery,
      TermsFilterBuilder typeFilter,
      FilterBuilder filters)
  {
    AndFilterBuilder allFilters = new AndFilterBuilder(filters, typeFilter);

    final FilteredQueryBuilder filteredQuery = QueryBuilders
        .filteredQuery(
            baseQuery,
            allFilters);
    CountRequestBuilder search = client.prepareCount(indexNames)
        .setQuery(filteredQuery);
    CountResponse response = search.execute().actionGet();
    return response.getCount();
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

  public String getCluster()
  {
    return cluster;
  }

  @Value("${elasticsearch.cluster}")
  public void setCluster(String cluster)
  {
    this.cluster = cluster;
  }

  public String[] getNodes()
  {
    return nodes;
  }

  @Value("${elasticsearch.nodes}")
  public void setNodes(String[] nodes)
  {
    this.nodes = nodes;
  }

  public String[] getIndexNames()
  {
    return indexNames;
  }

  public void setIndexNames(String[] indexNames)
  {
    this.indexNames = indexNames;
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

  public String[] getDefaultTypeFilterValues()
  {
    return defaultTypeFilterValues;
  }

  @Value("${elasticsearch.default.filter.type}")
  public void setDefaultTypeFilterValues(String[] defaultTypeFilterValues)
  {
    this.defaultTypeFilterValues = defaultTypeFilterValues;
  }
}
