package de.seitenbau.govdata.odp.registry.ckan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.impl.AccessServiceImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.LicenceImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.AccessServiceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.model.AccessService;
import de.seitenbau.govdata.odp.registry.model.Licence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Util
{
  private static final ObjectMapper OM = new ObjectMapper();

  private static final Licence NO_LICENCE_AVAILABLE = createLicence("license-id-not-set");

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

  public static List<AccessService> readJsonAccessServiceList(JsonNode node, ODRClient odrClient)
  {
    ArrayList<AccessService> result = new ArrayList<>();

    if (node.isTextual() && !node.textValue().isEmpty())
    {
      String serialized = node.textValue();

      try
      {
        JsonNode jsonList = OM.readTree(serialized);
        if (jsonList.isArray())
        {
          for (JsonNode value : jsonList)
          {
            result
                .add(new AccessServiceImpl(odrClient, ODRClientImpl.convert(value, AccessServiceBean.class)));
          }
        }
        else
        {
          result.add(
              new AccessServiceImpl(odrClient, ODRClientImpl.convert(jsonList, AccessServiceBean.class)));
        }
      }
      catch (IOException e)
      {
        log.error("Error deserializing " + serialized + ", not a List! Msg: " + e.getMessage(), e);
      }

    }

    return result;
  }

  public static String writeJsonAccessServiceList(List<AccessService> list) throws JsonProcessingException
  {
    ArrayNode node = OM.createArrayNode();
    for (AccessService accessService : list)
    {
      node.add(ODRClientImpl.convert(accessService));
    }

    return OM.writeValueAsString(node);
  }

  public static Licence initLicense(Licence license, String licenseId, ODRClient odrClient)
  {
    if (license == null)
    {
      if (licenseId != null)
      {
        Licence licenceIfUnknown = createLicence(licenseId);
        List<Licence> licences = odrClient.listLicenses();
        license =
            licences.stream().filter(l -> l.getId().equals(licenseId)).findFirst().orElse(licenceIfUnknown);
      }
      else
      {
        license = NO_LICENCE_AVAILABLE;
      }
    }

    return license;
  }

  private static Licence createLicence(String licenceId)
  {
    LicenceBean bean = new LicenceBean();
    bean.setId(licenceId);
    return new LicenceImpl(bean);
  }

  /**
   * Returns the code value for availability if a value is present and represents a valid URI.
   *
   * @param availability string.
   * @return the code value of the availability, otherwise null.
   */
  public static String getShortendAvailability(String availability)
  {

    Optional<DcatApAvailability> shortendAvailability =
        DcatApAvailability.getFromAvailability(availability);

    if (shortendAvailability.isPresent())
    {
      return shortendAvailability.get().name();
    }

    return null;

  }
}
