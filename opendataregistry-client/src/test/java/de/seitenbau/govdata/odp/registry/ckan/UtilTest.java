package de.seitenbau.govdata.odp.registry.ckan;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.node.TextNode;

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

}
