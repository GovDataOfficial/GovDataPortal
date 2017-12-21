package de.seitenbau.govdata.search.index.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import de.seitenbau.govdata.search.index.LiferayDocumentFields;

public class BlogFilterTest
{
  private BlogFilter sutFilter;
  
  private Document documentMock;
  
  @Before
  public void setUp()
  {
    sutFilter = new BlogFilter();
    documentMock = mock(Document.class);
  }
  
  @Test
  public void testDocumentIsRelevant()
  {
    when(documentMock.get(Field.STATUS)).thenReturn("0");
    when(documentMock.get(LiferayDocumentFields.FIELD_VISIBLE)).thenReturn("true");
    
    Boolean result = sutFilter.isRelevantForIndex(documentMock);
    
    assertTrue(result);
  }
  
  @Test
  public void testDocumentIsNotRelevantDueToStatus()
  {
    when(documentMock.get(Field.STATUS)).thenReturn("8");
    when(documentMock.get(LiferayDocumentFields.FIELD_VISIBLE)).thenReturn("true");
    
    Boolean result = sutFilter.isRelevantForIndex(documentMock);
    
    assertFalse(result);
  }
  
  @Test
  public void testDocumentIsNotRelevantDueToVisibility()
  {
    when(documentMock.get(Field.STATUS)).thenReturn("0");
    when(documentMock.get(LiferayDocumentFields.FIELD_VISIBLE)).thenReturn("false");
    
    Boolean result = sutFilter.isRelevantForIndex(documentMock);
    
    assertFalse(result);
  }
  
  @Test
  public void testDocumentIsNotRelevantDueToAllCheckedAttributes()
  {
    when(documentMock.get(Field.STATUS)).thenReturn("8");
    when(documentMock.get(LiferayDocumentFields.FIELD_VISIBLE)).thenReturn("false");
    
    Boolean result = sutFilter.isRelevantForIndex(documentMock);
    
    assertFalse(result);
  }

}
