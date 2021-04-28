package de.seitenbau.govdata.search.index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.Gson;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.DateFormatFactory;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import de.seitenbau.govdata.search.adapter.SearchService;
import de.seitenbau.govdata.search.index.filter.FilterProxy;
import de.seitenbau.govdata.search.index.queue.adapter.IndexQueueAdapterServiceRESTResource;
import de.seitenbau.serviceportal.common.api.RestUserMetadata;
import de.seitenbau.serviceportal.common.messaging.Document;
import de.seitenbau.serviceportal.common.messaging.SearchIndexEntry;

@RunWith(MockitoJUnitRunner.class)
@Ignore("Wegen java.lang.NoClassDefFoundError: com/liferay/petra/sql/dsl/query/DSLQuery nach "
    + "der grundsätzlichen Umstellung der Maven-Abhängigkeiten deaktiviert. Soll mit GOVDATA-2489 " +
    "gd-search Portlet angepasst/korrigiert werden.")
public class GovDataSearchIndexWriterTest
{
  private static final String LIFERAY_INDEX_DATE_FORMAT_PATTERN = "yyyyMMddHHmmss";

  private static String DEFAULT_INDEXNAME = "govdata";
  
  private static String DEFAULT_MANDANT = "1";
  
  @SuppressWarnings("serial")
  private static class MetadataType extends HashMap<String, String>{};

  @Mock
  private Props propsMock;

  @Mock
  private FastDateFormatFactory fastDateFormatFactoryMock;

  @Mock
  private DateFormatFactory dateFormatFactoryMock;

  @Mock
  private BeanLocator beanLocatorMock;

  @Mock
  private AssetTagLocalService assetTagLocalServiceMock;

  @Mock
  private IndexQueueAdapterServiceRESTResource indexQueueServiceMock;

  @Mock
  private SearchService searchServiceMock;

  @Mock
  private FilterProxy filterProxyMock;

  @Mock
  private SearchContext searchContextMock;
  
  @InjectMocks
  private GovDataSearchIndexWriter sutWriter;
  
  @Before
  public void setUp()
  {
    sutWriter.setIndexName(DEFAULT_INDEXNAME);
  }
  
  @Test
  public void testDeletePortletDocuments() throws SearchException
  {
    String portletId = "42";
    String uid1 = "12345";
    String uid2 = "54321";
    
    List<String> uids = new ArrayList<String>();
    uids.add(uid1);
    uids.add(uid2);
    
    when(searchServiceMock.findPortalContentIdsByPortletId(portletId)).thenReturn(uids);
    
    sutWriter.deleteEntityDocuments(searchContextMock, portletId);
    
    ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<SearchIndexEntry> searchIndexEntryCaptor = ArgumentCaptor.forClass(SearchIndexEntry.class);
    
    verify(indexQueueServiceMock, times(2)).delete(
        any(RestUserMetadata.class), 
        idCaptor.capture(), 
        searchIndexEntryCaptor.capture());
    
    assertThat(idCaptor.getAllValues().get(0)).isEqualTo(uid1);
    assertThat(idCaptor.getAllValues().get(1)).isEqualTo(uid2);
    
    SearchIndexEntry firstSearchIndexEntry = searchIndexEntryCaptor.getAllValues().get(0);
    assertThat(firstSearchIndexEntry.getDocument().getId()).isEqualTo(uid1);
    assertThat(firstSearchIndexEntry.getDocument().getMandant()).isEqualTo(DEFAULT_MANDANT);
    assertThat(firstSearchIndexEntry.getIndexName()).isEqualTo(DEFAULT_INDEXNAME);
    assertThat(firstSearchIndexEntry.getType()).isEqualTo(PortalIndexConstants.INDEX_TYPE_PORTAL);
    
    SearchIndexEntry secondSearchIndexEntry = searchIndexEntryCaptor.getAllValues().get(1);
    assertThat(secondSearchIndexEntry.getDocument().getId()).isEqualTo(uid2);
    assertThat(secondSearchIndexEntry.getDocument().getMandant()).isEqualTo(DEFAULT_MANDANT);
    assertThat(secondSearchIndexEntry.getIndexName()).isEqualTo(DEFAULT_INDEXNAME);
    assertThat(secondSearchIndexEntry.getType()).isEqualTo(PortalIndexConstants.INDEX_TYPE_PORTAL);
  }
  
  @Test
  public void testDeletePortletDocuments_NoDocuments() throws SearchException
  {
    String portletId = "42";
    
    List<String> uids = new ArrayList<String>();
    
    when(searchServiceMock.findPortalContentIdsByPortletId(portletId)).thenReturn(uids);
    
    sutWriter.deleteEntityDocuments(searchContextMock, portletId);
    
    verify(indexQueueServiceMock, never()).delete(
        any(RestUserMetadata.class), 
        any(String.class), 
        any(SearchIndexEntry.class));
    
  }

  @Test
  public void buildSearchIndexEntryFromLiferayDocument_only_uuid() throws Exception
  {
    /* prepare */
    String portletId = "42";
    String entryId = "100";
    String uuid = portletId + "_PORTLET_" + entryId;

    PropsUtil.setProps(propsMock);
    new FastDateFormatFactoryUtil().setFastDateFormatFactory(fastDateFormatFactoryMock);
    DocumentImpl document = new DocumentImpl();
    document.addUID(portletId, entryId);

    /* execute */
    SearchIndexEntry result = sutWriter.buildSearchIndexEntryFromLiferayDocument(document);

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getType()).isEqualTo(PortalIndexConstants.INDEX_TYPE_PORTAL);
    Assertions.assertThat(result.getIndexName()).isEqualTo(DEFAULT_INDEXNAME);
    Assertions.assertThat(result.getVersion()).isNull();
    Document searchEntryDocument = result.getDocument();
    Assertions.assertThat(searchEntryDocument).isNotNull();
    Assertions.assertThat(searchEntryDocument.getMandant()).isEqualTo(DEFAULT_MANDANT);
    Assertions.assertThat(searchEntryDocument.getId()).isEqualTo(uuid);
    Assertions.assertThat(searchEntryDocument.getTags()).isEmpty();
    Assertions.assertThat(searchEntryDocument.getSections()).isEmpty();
    Assertions.assertThat(searchEntryDocument.getTitle()).isNull();
    Assertions.assertThat(searchEntryDocument.getPreamble()).isNull();
  }

  @Test
  public void buildSearchIndexEntryFromLiferayDocument() throws Exception
  {
    /* prepare */
    String portletId = "42";
    String entryId = "100";
    String uuid = portletId + "_PORTLET_" + entryId;
    String entryClassPk = "200";
    String version = "1.0";
    String title = "My title";
    String content = "My content";
    String entryClassName = BlogsEntry.class.getName();
    String[] tagNameArray = new String[] {"one", "two", "three"};

    // Mocks
    PropsUtil.setProps(propsMock);
    when(propsMock.get(PropsKeys.INDEX_DATE_FORMAT_PATTERN))
        .thenReturn(LIFERAY_INDEX_DATE_FORMAT_PATTERN);
    new FastDateFormatFactoryUtil().setFastDateFormatFactory(fastDateFormatFactoryMock);
    new DateFormatFactoryUtil().setDateFormatFactory(dateFormatFactoryMock);
    DateFormat df = new SimpleDateFormat(LIFERAY_INDEX_DATE_FORMAT_PATTERN);
    when(fastDateFormatFactoryMock.getSimpleDateFormat(LIFERAY_INDEX_DATE_FORMAT_PATTERN))
        .thenReturn(df);
    when(dateFormatFactoryMock.getSimpleDateFormat(LIFERAY_INDEX_DATE_FORMAT_PATTERN))
        .thenReturn(df);
    // Tags
    PortalBeanLocatorUtil.setBeanLocator(beanLocatorMock);
    when(beanLocatorMock.locate(AssetTagLocalService.class.getCanonicalName()))
        .thenReturn(assetTagLocalServiceMock);
    when(assetTagLocalServiceMock.getTagNames(entryClassName, Long.parseLong(entryClassPk)))
        .thenReturn(tagNameArray);

    // Document
    DocumentImpl document = new DocumentImpl();
    document.addUID(portletId, entryId);
    // Field.VERSION
    document.add(new Field(Field.VERSION, version));
    // Field.TITLE
    document.add(new Field(Field.TITLE, title));
    // Field.CONTENT
    document.add(new Field(Field.CONTENT, content));
    // Field.MODIFIED_DATE
    document.add(new Field(Field.MODIFIED_DATE, "20150819035214")); // UTC +2:00
    // Field.ENTRY_CLASS_NAME
    document.add(new Field(Field.ENTRY_CLASS_NAME, entryClassName));
    // Field.ENTRY_CLASS_NAME
    document.add(new Field(Field.ENTRY_CLASS_PK, entryClassPk));

    /* execute */
    SearchIndexEntry result = sutWriter.buildSearchIndexEntryFromLiferayDocument(document);

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getType()).isEqualTo(PortalIndexConstants.INDEX_TYPE_PORTAL);
    Assertions.assertThat(result.getIndexName()).isEqualTo(DEFAULT_INDEXNAME);
    Assertions.assertThat(result.getVersion()).isEqualTo(version);
    Document searchEntryDocument = result.getDocument();
    Assertions.assertThat(searchEntryDocument).isNotNull();
    Assertions.assertThat(searchEntryDocument.getMandant()).isEqualTo(DEFAULT_MANDANT);
    Assertions.assertThat(searchEntryDocument.getId()).isEqualTo(uuid);
    Assertions.assertThat(searchEntryDocument.getTags()).isEqualTo(tagNameArray);
    Assertions.assertThat(searchEntryDocument.getSections()).isEmpty();
    Assertions.assertThat(searchEntryDocument.getTitle()).isEqualTo(title);
    Assertions.assertThat(searchEntryDocument.getPreamble()).isEqualTo(content);
    Map<String, String> metadataExpected = new HashMap<String, String>();
    metadataExpected.put("uid", uuid);
    metadataExpected.put("type", "blog");
    metadataExpected.put(Field.ENTRY_CLASS_NAME, entryClassName);
    metadataExpected.put(Field.ENTRY_CLASS_PK, entryClassPk);
    String modified = "2015-08-19T01:52:14"; // UTC
    metadataExpected.put(Field.MODIFIED_DATE, modified);
    String metadataJsonString = (String) searchEntryDocument.getMetadata();
    Map<String, String> metadata = new Gson().fromJson(metadataJsonString, MetadataType.class);
    Assertions.assertThat(metadata).isEqualTo(metadataExpected);
  }
}
