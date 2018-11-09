package de.seitenbau.govdata.dataset.details.beans;

import org.assertj.core.api.Assertions;
import org.elasticsearch.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import de.seitenbau.govdata.odp.registry.model.Contact;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.RoleEnumType;

/**
 * Tests für {@link CurrentMetadataContact}
 * 
 * @author rnoerenberg
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CurrentMetadataContactTest
{
  private static final String CREATOR_TEST_EMAIL = "creator@email.de";

  private static final String CREATOR_TESTNAME = "creatorName";

  private static final String PUBLISHER_TEST_EMAIL = "publisher@email.de";

  private static final String PUBLISHER_TESTNAME = "publisherName";

  @Mock
  private Metadata metadata;

  @Mock
  private Contact creator;

  @Mock
  private Contact publisher;

  @Before
  public void setup() throws Exception
  {
    Mockito.when(creator.getRole()).thenReturn(RoleEnumType.CREATOR);
    Mockito.when(creator.getName()).thenReturn(CREATOR_TESTNAME);
    Mockito.when(creator.getEmail()).thenReturn(CREATOR_TEST_EMAIL);
    Mockito.when(publisher.getRole()).thenReturn(RoleEnumType.PUBLISHER);
    Mockito.when(publisher.getName()).thenReturn(PUBLISHER_TESTNAME);
    Mockito.when(publisher.getEmail()).thenReturn(PUBLISHER_TEST_EMAIL);

    Mockito.when(metadata.getContacts()).thenReturn(Lists.newArrayList(creator, publisher));
    Mockito.when(metadata.getAuthor()).thenReturn("author@test.de");
  }

  @Test
  public void creator_author_fallback_null() throws Exception
  {
    /* prepare */
    
    /* execute */
    CurrentMetadataContact result = new CurrentMetadataContact(metadata);
    
    /* verify */
    Assertions.assertThat(result.getName()).isEqualTo(PUBLISHER_TESTNAME);
    Assertions.assertThat(result.getEmail()).isEqualTo(PUBLISHER_TEST_EMAIL);
  }
}
