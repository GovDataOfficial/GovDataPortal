package de.seitenbau.govdata.search.index;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.NotFoundException;

import com.google.gson.Gson;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.PropsUtil;

import de.seitenbau.govdata.common.api.RestUserMetadata;
import de.seitenbau.govdata.common.client.impl.RestCallFailedException;
import de.seitenbau.govdata.common.json.DateUtil;
import de.seitenbau.govdata.common.messaging.SearchIndexEntry;
import de.seitenbau.govdata.index.queue.adapter.IndexQueueAdapterServiceRESTResource;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.search.index.filter.FilterProxy;
import de.seitenbau.govdata.search.index.mapper.ClassToTypeMapper;
import de.seitenbau.govdata.search.index.util.SearchIndexUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Enthält die Logik, um im Portal-Index Liferay-Dokumente anzulegen, zu aktualisieren und zu
 * löschen.
 * 
 * @author arothbauer
 * @author rnoerenberg
 *
 */
@Slf4j
public class GovDataSearchIndexWriter implements IndexerPostProcessor
{
  private static final Locale LOCALE_USED = Locale.GERMANY;

  private IndexQueueAdapterServiceRESTResource indexClient =
      ApplicationContextProvider.getApplicationContext().getBean(IndexQueueAdapterServiceRESTResource.class);
  
  private String indexName = PropsUtil.get(SearchConsts.PARAM_ELASTICSEARCH_LIFERAY_INDEX_NAME);

  private SearchIndexUtil searchIndexUtil =
      ApplicationContextProvider.getApplicationContext().getBean(SearchIndexUtil.class);

  private FilterProxy filterProxy =
      ApplicationContextProvider.getApplicationContext().getBean(FilterProxy.class);

  @Override
  public void postProcessContextBooleanFilter(BooleanFilter booleanFilter, SearchContext searchContext)
      throws Exception
  {
    if (log.isDebugEnabled())
    {
      log.debug("postProcessContextBooleanFilter()");
    }
  }

  @Override
  public void postProcessDocument(Document document, Object object) throws Exception
  {
    SearchIndexEntry entry = buildSearchIndexEntryFromLiferayDocument(document);
    RestUserMetadata ruMetadata = new RestUserMetadata(IndexConstants.INDEX_MANDANT);
    String uid = document.getUID();

    if (!document.get(Field.REMOVED_DATE).isEmpty()
        || !filterProxy.isRelevantForIndex(document))
    {
      try
      {
        deleteDocument(entry, ruMetadata, uid);
      }
      catch (Exception e)
      {
        log.error("Could not delete document uid=" + uid, e);
      }
    }
    else
    {
      try
      {
        addDocument(entry, ruMetadata, uid);
      }
      catch (Exception e)
      {
        log.error("Could not add document uid=" + uid, e);
      }
    }
  }

  @Override
  public void postProcessFullQuery(BooleanQuery fullQuery, SearchContext searchContext) throws Exception
  {
    if (log.isDebugEnabled())
    {
      log.debug(" postProcessFullQuery()");
    }
  }

  @Override
  public void postProcessSearchQuery(BooleanQuery searchQuery, BooleanFilter booleanFilter,
      SearchContext searchContext) throws Exception
  {
    if (log.isDebugEnabled())
    {
      log.debug(" postProcessSearchQuery()");
    }
  }

  @Override
  public void postProcessSummary(Summary summary, Document document, Locale locale, String snippet)
  {
    if (log.isDebugEnabled())
    {
      log.debug("postProcessSummary()");
    }
  }

  private SearchIndexEntry createSearchIndexEntryWithBasicInformation(String uid)
  {
    SearchIndexEntry entry = searchIndexUtil.createSearchIndexEntryWithBasicInformation(uid, indexName,
        IndexConstants.INDEX_TYPE_PORTAL);
    return entry;
  }

  protected SearchIndexEntry buildSearchIndexEntryFromLiferayDocument(Document document)
  {
    SearchIndexEntry entry = createSearchIndexEntryWithBasicInformation(document.getUID());
    de.seitenbau.govdata.common.messaging.Document msgDocument = entry.getDocument();

    // Map document fields. Most are mapped to metadata, but some may be set on specific fields
    Map<String, String> metadata = new HashMap<>();
    Set<String> keys = document.getFields().keySet();
    for (String key : keys)
    {
      if (key.equals(Field.VERSION))
      {
        entry.setVersion(document.get(key));
      }
      else if (key.equals(Field.getLocalizedName(LOCALE_USED, Field.TITLE)))
      {
        msgDocument.setTitle(document.get(key));
      }
      else if (key.equals(Field.getLocalizedName(LOCALE_USED, Field.CONTENT)))
      {
        msgDocument.setPreamble(document.get(key));
      }
      else if (key.equals(Field.MODIFIED_DATE))
      {
        try
        {
          metadata.put(Field.MODIFIED_DATE, DateUtil.formatDateUTC(document.getDate(key)));
        }
        catch (ParseException e)
        {
          // don't modify the field...
        }
      }
      else if (key.equals(Field.ENTRY_CLASS_NAME))
      {
        String type = new ClassToTypeMapper().getTypeForClass(document.get(Field.ENTRY_CLASS_NAME));
        metadata.put(IndexConstants.METADATA_FIELD_TYPE, type);
        metadata.put(Field.ENTRY_CLASS_NAME, document.get(key));
      }
      else
      {
        metadata.put(key, document.get(key));
      }
    }
    // Tags
    String[] tagList = new String[0];
    try
    {
      String className = document.get(Field.ENTRY_CLASS_NAME);
      if (className != null)
      {
        long classPk = Long.parseLong(document.get(Field.ENTRY_CLASS_PK));
        tagList = AssetTagLocalServiceUtil.getTagNames(className, classPk);
      }
    }
    catch (Exception e)
    {
      log.warn("Fehler beim Lesen der Tags für das Dokument mit der ID {}. Details: {}", document.getUID(),
          e.getMessage());
    }
    msgDocument.setTags(tagList);

    // map metadata object to JSON-String
    msgDocument.setMetadata(new Gson().toJson(metadata));
    entry.setDocument(msgDocument);
    return entry;
  }

  private void throwOnDeletingExcpetion(String uid, Throwable cause) throws SearchException
  {
    throw new SearchException("Deleting document uid=" + uid + " failed.", cause);
  }

  private void addDocument(SearchIndexEntry entry, RestUserMetadata ruMetadata, String uid)
  {
    log.debug("addDocument: uid={}", uid);
    indexClient.save(ruMetadata, entry);
  }

  private void deleteDocument(SearchIndexEntry entry, RestUserMetadata ruMetadata, String uid)
      throws SearchException
  {
    log.debug("deleteDocument uid=" + uid);
    try
    {
      indexClient.deleteAndSendDeleteMessage(ruMetadata, uid, entry);
    }
    catch (RestCallFailedException restCallFailedException)
    {
      Throwable cause = restCallFailedException.getCause();
      if (!(cause instanceof NotFoundException))
      {

        throwOnDeletingExcpetion(uid, cause);
      }
    }
    catch (Exception e)
    {
      throwOnDeletingExcpetion(uid, e);
    }
  }

}
