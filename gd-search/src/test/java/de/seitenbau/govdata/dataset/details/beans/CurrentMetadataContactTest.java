package de.seitenbau.govdata.dataset.details.beans;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.seitenbau.govdata.data.api.ckan.dto.ContactDto;
import de.seitenbau.govdata.data.api.ckan.dto.MetadataDto;
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
  private static final String PUBLISHER_TEST_EMAIL = "publisher@email.de";

  private static final String PUBLISHER_TESTNAME = "publisherName";

  @Mock
  private MetadataDto metadata;

  @Mock
  private ContactDto creator;

  @Mock
  private ContactDto publisher;

  @Test
  public void creator_role_does_not_match() throws Exception
  {
    /* prepare */
    Mockito.when(creator.getRole()).thenReturn(RoleEnumType.CREATOR);

    Mockito.when(metadata.getContacts())
        .thenReturn(Arrays.stream(new ContactDto[] {creator, publisher}).collect(Collectors.toList()));

    /* execute */
    CurrentMetadataContact result = new CurrentMetadataContact(metadata);

    /* verify */
    Assertions.assertThat(result.getName()).isNull();
    Assertions.assertThat(result.getEmail()).isNull();
  }

  @Test
  public void creator_publisher_fallback_null() throws Exception
  {
    /* prepare */
    Mockito.when(creator.getRole()).thenReturn(RoleEnumType.CREATOR);
    Mockito.when(publisher.getRole()).thenReturn(RoleEnumType.PUBLISHER);
    Mockito.when(publisher.getName()).thenReturn(PUBLISHER_TESTNAME);
    Mockito.when(publisher.getEmail()).thenReturn(PUBLISHER_TEST_EMAIL);

    Mockito.when(metadata.getContacts())
        .thenReturn(Arrays.stream(new ContactDto[] {creator, publisher}).collect(Collectors.toList()));
    
    /* execute */
    CurrentMetadataContact result = new CurrentMetadataContact(metadata);
    
    /* verify */
    Assertions.assertThat(result.getName()).isEqualTo(PUBLISHER_TESTNAME);
    Assertions.assertThat(result.getEmail()).isEqualTo(PUBLISHER_TEST_EMAIL);
  }
}
