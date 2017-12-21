package de.seitenbau.govdata.search.index.filter;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import de.seitenbau.govdata.search.index.filter.Filter;
import de.seitenbau.govdata.search.index.filter.FilterProxy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Matchers.any;

public class FilterProxyTest
{
  private FilterProxy sutFilterProxy;
  
  private Filter filterMockOne;
  
  private Filter filterMockTwo;
  
  private Document documentMock;
  
  @Before
  public void setUp()
  {
    sutFilterProxy = new FilterProxy();
    documentMock = mock(Document.class);
    filterMockOne = mock(Filter.class);
    filterMockTwo = mock(Filter.class);
  }

  @Test
  public void testDocumentHasFilterAndIsRelevant()
  {
     when(documentMock.get(Field.ENTRY_CLASS_NAME)).thenReturn("de.any.package.SomeClassName");
     
     when(filterMockOne.isRelevantForIndex(documentMock)).thenReturn(true);
     
     Map<String, Filter> filterMap = new HashMap<String, Filter>();
     filterMap.put("de.any.package.SomeClassName", filterMockOne);
     filterMap.put("de.any.package.SomeOtherClassName", filterMockTwo);
     
     sutFilterProxy.setRegisteredFilter(filterMap);
     
     Boolean result = sutFilterProxy.isRelevantForIndex(documentMock);
     assertTrue(result);
     verify(filterMockOne, times(1)).isRelevantForIndex(documentMock);
     verify(filterMockTwo, never()).isRelevantForIndex(any(Document.class));
  }
  
  @Test
  public void testDocumentHasFilterAndIsNotRelevant()
  {
    when(documentMock.get(Field.ENTRY_CLASS_NAME)).thenReturn("de.any.package.SomeOtherClassName");
    
    when(filterMockTwo.isRelevantForIndex(documentMock)).thenReturn(false);
    
    Map<String, Filter> filterMap = new HashMap<String, Filter>();
    filterMap.put("de.any.package.SomeClassName", filterMockOne);
    filterMap.put("de.any.package.SomeOtherClassName", filterMockTwo);
    
    sutFilterProxy.setRegisteredFilter(filterMap);
    
    Boolean result = sutFilterProxy.isRelevantForIndex(documentMock);
    
    assertFalse(result);
    verify(filterMockOne, never()).isRelevantForIndex(any(Document.class));
    verify(filterMockTwo, times(1)).isRelevantForIndex(documentMock);
  }
  
  @Test
  public void testDocumentHasNoFilter()
  {
    when(documentMock.get(Field.ENTRY_CLASS_NAME)).thenReturn("de.any.package.SomeClassNameWithoutFilter");
    
    Map<String, Filter> filterMap = new HashMap<String, Filter>();
    filterMap.put("de.any.package.SomeClassName", filterMockOne);
    filterMap.put("de.any.package.SomeOtherClassName", filterMockTwo);
    
    sutFilterProxy.setRegisteredFilter(filterMap);
    
    Boolean result = sutFilterProxy.isRelevantForIndex(documentMock);
    
    assertFalse(result);
    verify(filterMockOne, never()).isRelevantForIndex(any(Document.class));
    verify(filterMockTwo, never()).isRelevantForIndex(any(Document.class));
  }

}
