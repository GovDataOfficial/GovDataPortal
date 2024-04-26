package de.seitenbau.govdata.search.index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.Gson;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import de.seitenbau.govdata.common.api.RestUserMetadata;
import de.seitenbau.govdata.common.messaging.Document;
import de.seitenbau.govdata.common.messaging.SearchIndexEntry;
import de.seitenbau.govdata.index.queue.adapter.IndexQueueAdapterServiceRESTResource;
import de.seitenbau.govdata.search.index.filter.FilterProxy;

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
  private BeanLocator beanLocatorMock;

  @Mock
  private AssetTagLocalService assetTagLocalServiceMock;

  @Mock
  private IndexQueueAdapterServiceRESTResource indexQueueServiceMock;

  @Mock
  private FilterProxy filterProxyMock;

  @Mock
  private SearchContext searchContextMock;
  
  @InjectMocks
  private GovDataSearchIndexWriter sutWriter;

  @Test
  public void testpostProcessAddDocument() throws Exception
  {
    /* prepare */
    DocumentImpl document = createAndGetDocument();


    sutWriter.postProcessDocument(document, new Object());

    ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<SearchIndexEntry> searchIndexEntryCaptor = ArgumentCaptor.forClass(SearchIndexEntry.class);

    verify(indexQueueServiceMock, times(0)).deleteAndSendDeleteMessage(
        any(RestUserMetadata.class),
        idCaptor.capture(),
        searchIndexEntryCaptor.capture());
    verify(indexQueueServiceMock, times(1)).save(
        any(RestUserMetadata.class),
        searchIndexEntryCaptor.capture());

    assertThat(searchIndexEntryCaptor.getAllValues().size()).isEqualTo(1);
    SearchIndexEntry firstSearchIndexEntry = searchIndexEntryCaptor.getAllValues().get(0);
    assertThat(firstSearchIndexEntry.getDocument().getId()).isEqualTo(document.getUID());
    assertThat(firstSearchIndexEntry.getDocument().getMandant()).isEqualTo(DEFAULT_MANDANT);
    assertThat(firstSearchIndexEntry.getIndexName()).isEqualTo(DEFAULT_INDEXNAME);

  }

  @Test
  public void testpostProcessDeleteDocument() throws Exception
  {
    /* prepare */
    DocumentImpl document = createAndGetDocument();
    // Field.REMOVED_DATE
    document.add(new Field(Field.REMOVED_DATE, "20150819035215")); // UTC +2:00

    sutWriter.postProcessDocument(document, new Object());

    ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<SearchIndexEntry> searchIndexEntryCaptor = ArgumentCaptor.forClass(SearchIndexEntry.class);

    verify(indexQueueServiceMock, times(0)).save(
        any(RestUserMetadata.class),
        searchIndexEntryCaptor.capture());
    verify(indexQueueServiceMock, times(1)).deleteAndSendDeleteMessage(
        any(RestUserMetadata.class),
        idCaptor.capture(),
        searchIndexEntryCaptor.capture());

    assertThat(idCaptor.getAllValues().size()).isEqualTo(1);
    assertThat(idCaptor.getAllValues().get(0)).isEqualTo(document.getUID());

    assertThat(searchIndexEntryCaptor.getAllValues().size()).isEqualTo(1);
    SearchIndexEntry firstSearchIndexEntry = searchIndexEntryCaptor.getAllValues().get(0);
    assertThat(firstSearchIndexEntry.getDocument().getId()).isEqualTo(document.getUID());
    assertThat(firstSearchIndexEntry.getDocument().getMandant()).isEqualTo(DEFAULT_MANDANT);
    assertThat(firstSearchIndexEntry.getIndexName()).isEqualTo(DEFAULT_INDEXNAME);

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
    Assertions.assertThat(result.getType()).isEqualTo(IndexConstants.INDEX_TYPE_PORTAL);
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
    DateFormat df = new SimpleDateFormat(LIFERAY_INDEX_DATE_FORMAT_PATTERN);
    when(fastDateFormatFactoryMock.getSimpleDateFormat(LIFERAY_INDEX_DATE_FORMAT_PATTERN))
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
    Assertions.assertThat(result.getType()).isEqualTo(IndexConstants.INDEX_TYPE_PORTAL);
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

  private DocumentImpl createAndGetDocument()
  {
    String portletId = "42";
    String entryId = "100";
    String uuid = portletId + "_PORTLET_" + entryId;
    String entryClassPk = "20013";
    String version = "1.0";
    String title = "My title";
    String content = "My content";
    String entryClassName = BlogsEntry.class.getName();

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

    return document;
  }
}
