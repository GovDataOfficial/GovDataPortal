package de.seitenbau.govdata.odp.registry.ckan;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.node.TextNode;

import de.seitenbau.govdata.odp.registry.ckan.impl.AccessServiceImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.AccessServiceBean;
import de.seitenbau.govdata.odp.registry.model.AccessService;

@RunWith(MockitoJUnitRunner.class)
public class UtilTest {
  
  @Test
  public void readJsonList() throws Exception {
    // Single Value
    {
      TextNode singleValueInput = new TextNode("hallo");
      List<String> list = Util.readJsonList(singleValueInput);
      Assertions.assertThat(list).containsOnly("hallo");
    }

    // serialized JSON List
    {
      TextNode serializedList = new TextNode("[\"hallo\",\"welt\"]");
      List<String> list = Util.readJsonList(serializedList);
      Assertions.assertThat(list).containsOnly("hallo", "welt");
    }
  }

  @Test
  public void readJsonAccessServiceList() throws Exception
  {
    // Invalid Json
    {
      TextNode invalidJsonInput = new TextNode("Invalid Json");
      List<AccessService> list = Util.readJsonAccessServiceList(invalidJsonInput, null);
      Assertions.assertThat(list).isEmpty();
    }

    AccessServiceBean accessServiceBean = new AccessServiceBean();
    accessServiceBean
        .setAvailability("http://publications.europa.eu/resource/authority/planned-availability/AVAILABLE");
    accessServiceBean
        .setTitle("Sparql-end Point");
    accessServiceBean.setEndpointDescription("SPARQL url description");
    accessServiceBean
        .setAccessRights("http://publications.europa.eu/resource/authority/access-right/PUBLIC");
    accessServiceBean
        .setDescription("This SPARQL end point allow to directly query the EU Whoiswho content");
    accessServiceBean
        .setEndpointUrls(Arrays.asList("http://publications.europa.eu/webapi/rdf/sparql"));
    accessServiceBean
        .setServesDataset(Arrays.asList(
            "http://data.europa.eu/88u/dataset/eu-whoiswho-the-official-directory-of-the-european-union"));

    AccessService accessService = new AccessServiceImpl(accessServiceBean);
    List<AccessService> expectedAccessServices = Arrays.asList(accessService);

    String singleValueInput = "{"
        + "\"availability\":\"http://publications.europa.eu/resource/authority/planned-availability/AVAILABLE\","
        + "\"title\":\"Sparql-end Point\","
        + "\"endpoint_description\":\"SPARQL url description\","
        + "\"access_rights\":\"http://publications.europa.eu/resource/authority/access-right/PUBLIC\","
        + "\"description\":\"This SPARQL end point allow to directly query the EU Whoiswho content\","
        + "\"endpoint_url\":[\"http://publications.europa.eu/webapi/rdf/sparql\"],"
        + "\"serves_dataset\":[\"http://data.europa.eu/88u/dataset/eu-whoiswho-the-official-directory-of-the-european-union\"]"
        + "}";

    // Single Value
    {

      TextNode serializedObject = new TextNode(singleValueInput);
      List<AccessService> list = Util.readJsonAccessServiceList(serializedObject, null);

      this.assertAccessService(list, expectedAccessServices);
    }

    // serialized JSON List
    {
      TextNode serializedList = new TextNode("[" + singleValueInput + "]");
      List<AccessService> list = Util.readJsonAccessServiceList(serializedList, null);

      this.assertAccessService(list, expectedAccessServices);

    }
  }

  private void assertAccessService(List<AccessService> actualList, List<AccessService> expectedList)
  {
    Assertions.assertThat(actualList).size().isEqualTo(expectedList.size());

    Assertions.assertThat(actualList).usingRecursiveComparison().isEqualTo(expectedList);
  }
}
