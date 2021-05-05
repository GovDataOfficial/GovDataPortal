package de.seitenbau.govdata.shacl.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import de.seitenbau.govdata.navigation.PortletUtil;
import de.seitenbau.govdata.shacl.ShaclValidator;
import de.seitenbau.govdata.shacl.util.ShaclQueryTemplates;

@Repository
public class ShaclValidatorImpl implements ShaclValidator
{
  private static final String API_METHOD = "validate";
  
  private static final Logger LOG = LoggerFactory.getLogger(ShaclValidator.class);

  private final String shaclValidatorEndpoint = PortletUtil.getLinkToShaclValidatorEndpoint();

  private final String shaclValidatorProfileType = PortletUtil.getValidatorProfileType();

  private CloseableHttpClient httpClient = null;

  @PostConstruct
  public void init()
  {
    httpClient = HttpClients.createDefault();
  }

  @PreDestroy
  void shutdown()
  {
    HttpClientUtils.closeQuietly(httpClient);
  }

  @Override
  public Model validate(String uri, Model model, String ownerOrgId)
  {
    final String method = "validate() : ";
    LOG.trace("{}Start SHACL Validation. uri: {}, ownerOrgId: {}.", method, uri, ownerOrgId);

    Model rdfModel = null;
    String reportQuery = ShaclQueryTemplates.getReportQuery(uri, ownerOrgId);

    StringWriter sr = new StringWriter();
    RDFDataMgr.write(sr, model, RDFFormat.RDFXML_ABBREV);
    // Escape double quotes and remove line endings for JSON content
    String contentToValidate = sr.toString().replaceAll("\"", "\\\\\"").replace(System.lineSeparator(), "");

    String jsonPayload = String.format(
        "{\"contentToValidate\":\"%s\",\"embeddingMethod\": \"STRING\","
            + "\"contentSyntax\": \"application/rdf+xml\",\"validationType\":\"%s\",\"reportQuery\":\"%s\"}",
        contentToValidate, shaclValidatorProfileType, reportQuery);

    try
    {
      HttpPost post = new HttpPost(shaclValidatorEndpoint + "/" + API_METHOD);
      HttpEntity entity =
          EntityBuilder.create().setText(jsonPayload).setContentType(ContentType.APPLICATION_JSON).build();
      post.setEntity(entity);

      LOG.debug("Send post request to SHACL-validator");
      CloseableHttpResponse response = httpClient.execute(post);
      try
      {

        if (response.getStatusLine().getStatusCode() == 200)
        {
          LOG.debug("Generate RDF-Model from response");
          String responseString = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
          rdfModel = ModelFactory.createDefaultModel();
          StringReader r = new StringReader(responseString);
          RDFDataMgr.read(rdfModel, r, null, RDFLanguages.RDFXML);
        }
        else
        {
          LOG.error("{} SHACL Validator responded with Status {}: ", method,
              response.getStatusLine().getStatusCode());
        }
      }
      finally
      {
        HttpClientUtils.closeQuietly(response);
      }
    }
    catch (IOException e)
    {
      LOG.error("{} Problem while sending request to SHACL-validator: {}", method, e.getMessage());
    }

    LOG.trace(method + "End");
    return rdfModel;
  }

  @Override
  public boolean isAvailable()
  {
    final String method = "isAvailable() : ";
    LOG.trace(method + "Start");
    try (Socket socket = new Socket())
    {
      // try to send a ping to the shacl-validator
      URL url = new URL(shaclValidatorEndpoint);
      socket.connect(new InetSocketAddress(url.getHost(), url.getPort()), 5000);
      LOG.info("SHACL support is ENABLED!");
      LOG.trace(method + "End");
      return true;
    }
    catch (MalformedURLException e)
    {
      LOG.info("{} Endpoint for SHACL-Validation is not defined. SHACL support is disabled!", method);
    }
    catch (IOException e)
    {
      LOG.error("{} SHACL-Validator is not reachable. SHACL support is disabled: {}", method, e.getMessage());
    }
    LOG.trace(method + "End");
    return false;
  }
}
