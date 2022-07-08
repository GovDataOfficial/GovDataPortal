package de.seitenbau.govdata.odp.registry.ckan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Util
{
  private static final ObjectMapper OM = new ObjectMapper();

  private Util()
  {
    // util class
  }
  // CHECKSTYLE:OFF

  public static List<String> readJsonList(JsonNode node) {
    ArrayList<String> result = new ArrayList<>();

    if(node.isTextual() && !node.textValue().isEmpty()) {
      String serialized = node.textValue();

      if(serialized.startsWith("[")) {
        try {
          JsonNode jsonList = OM.readTree(serialized);
          if(jsonList.isArray()) {
            for(JsonNode value : jsonList) {
              result.add(value.textValue());
            }
          } else {
            result.add(jsonList.textValue());
          }
        } catch (IOException e) {
          log.error("Error deserializing " + serialized + ", not a List! Msg: " + e.getMessage(), e);
        }
      } else {
        result.add(serialized);
      }
    }

    return result;
  }

  public static String writeJsonList(List<String> list) throws JsonProcessingException {
    ArrayNode node = OM.createArrayNode();
    for (String text : list) {
      node.add(text);
    }

    return OM.writeValueAsString(node);
  }
}
