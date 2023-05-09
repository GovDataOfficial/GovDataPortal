package de.seitenbau.govdata.search.index;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;

import de.seitenbau.govdata.common.api.RestUserMetadata;
import de.seitenbau.govdata.common.client.impl.RestCallFailedException;
import de.seitenbau.govdata.common.messaging.SearchIndexEntry;
import de.seitenbau.govdata.date.DateUtil;
import de.seitenbau.govdata.index.queue.adapter.IndexQueueAdapterServiceRESTResource;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.search.adapter.SearchService;
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
@Service
public class GovDataSearchIndexWriter implements IndexWriter
{
  private static final Locale LOCALE_USED = Locale.GERMANY;

  @Inject
  private IndexQueueAdapterServiceRESTResource indexClient;

  @Inject
  private SearchService searchService;

  @Inject
  private FilterProxy filterProxy;
  
  private String indexName;

  @Inject
  private SearchIndexUtil searchIndexUtil;
  
  @Override
  public void addDocument(SearchContext searchContext, Document document)
      throws SearchException
  {
    if (!filterProxy.isRelevantForIndex(document))
    {
      log.debug("addDocument: ignoring document uid={} (not relevant for index)", document.getUID());
      return;
    }
    log.debug("addDocument: uid={}", document.getUID());
    // same as update
    updateDocument(searchContext, document);
  }

  @Override
  public void addDocuments(SearchContext searchContext,
      Collection<Document> documents) throws SearchException
  {
    log.debug("addDocuments " + Integer.toString(documents.size()) + " documents.");
    for (Document document : documents)
    {
      try
      {
        addDocument(searchContext, document);
      }
      catch (Exception e)
      {
        // Would it be better to stop processing here?
        log.error("Could not add document uid=" + document.getUID(), e);
      }
    }
  }

  @Override
  public void deleteEntityDocuments(SearchContext searchContext, String className) throws SearchException
  {
    log.debug("deleteEntityDocuments className=" + className);
    List<String> uids = searchService.findPortalContentIdsByPortletId(className);
    if (uids.size() > 0)
    {
      deleteDocuments(searchContext, uids);
    }
  }

  @Override
  public void deleteDocument(SearchContext searchContext, String uid)
      throws SearchException
  {
    log.debug("deleteDocument uid=" + uid);
    
    SearchIndexEntry entry = createSearchIndexEntryWithBasicInformation(uid);

    RestUserMetadata ruMetadata = new RestUserMetadata(IndexConstants.INDEX_MANDANT);
    
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
    catch(Exception e)
    {
      throwOnDeletingExcpetion(uid, e);
    }
  }

  @Override
  public void deleteDocuments(SearchContext searchContext,
      Collection<String> uids) throws SearchException
  {
    log.debug("deleteDocuments " + Integer.toString(uids.size()) + " documents.");
    for (String uid : uids)
    {
      try
      {
        deleteDocument(searchContext, uid);
      }
      catch (Exception e)
      {
        // Would it be better to stop processing here?
        log.error("Could not delete document uid=" + uid, e);
      }
    }
  }

  @Override
  public void updateDocument(SearchContext searchContext, Document document)
      throws SearchException
  {
    if (!filterProxy.isRelevantForIndex(document))
    {
      log.debug("updateDocument: ignoring document uid={} (not relevant for index)", document.getUID());
      // as the document may already be in the index, we try to delete it
      deleteDocument(searchContext, document.getUID());
      return;
    }
    log.debug("updateDocument: uid=" + document.getUID());

    SearchIndexEntry entry = buildSearchIndexEntryFromLiferayDocument(document);

    RestUserMetadata ruMetadata = new RestUserMetadata(IndexConstants.INDEX_MANDANT);
    indexClient.save(ruMetadata, entry);
  }

  @Override
  public void updateDocuments(SearchContext searchContext,
      Collection<Document> documents) throws SearchException
  {
    log.debug("updateDocuments " + Integer.toString(documents.size()) + " documents.");
    for (Document document : documents)
    {
      try
      {
        updateDocument(searchContext, document);
      }
      catch (Exception e)
      {
        // Would it be better to stop processing here?
        log.error("Could not update document uid=" + document.getUID(), e);
      }
    }
  }

  @Override
  public void clearQuerySuggestionDictionaryIndexes(
      SearchContext searchContext) throws SearchException
  {
    log.debug("clearQuerySuggestionDictionaryIndexes");
  }

  @Override
  public void clearSpellCheckerDictionaryIndexes(SearchContext searchContext)
      throws SearchException
  {
    log.debug("clearSpellCheckerDictionaryIndexes");
  }

  @Override
  public void indexKeyword(SearchContext searchContext, float weight,
      String keywordType) throws SearchException
  {
    log.debug("indexKeyword");
  }

  @Override
  public void indexQuerySuggestionDictionaries(SearchContext searchContext)
      throws SearchException
  {
    log.debug("indexQuerySuggestionDictionaries");
  }

  @Override
  public void indexQuerySuggestionDictionary(SearchContext searchContext)
      throws SearchException
  {
    log.debug("indexQuerySuggestionDictionary");
  }

  @Override
  public void indexSpellCheckerDictionaries(SearchContext searchContext)
      throws SearchException
  {
    log.debug("indexSpellCheckerDictionaries");
  }

  @Override
  public void indexSpellCheckerDictionary(SearchContext searchContext)
      throws SearchException
  {
    log.debug("indexSpellCheckerDictionary");
  }

  @Override
  public void commit(SearchContext searchContext) throws SearchException
  {
    log.debug("commit");
  }

  @Override
  public void partiallyUpdateDocument(SearchContext searchContext, Document document) throws SearchException
  {
    log.debug("partiallyUpdateDocument");
    updateDocument(searchContext, document);
  }

  @Override
  public void partiallyUpdateDocuments(SearchContext searchContext, Collection<Document> documents)
      throws SearchException
  {
    log.debug("partiallyUpdateDocuments");
    updateDocuments(searchContext, documents);
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

  @Value(SearchConsts.CONFIG_ELASTICSEARCH_LIFERAY_INDEX_NAME)
  public void setIndexName(String indexName)
  {
    this.indexName = indexName;
  }
}
