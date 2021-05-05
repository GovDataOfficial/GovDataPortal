package de.seitenbau.govdata.fuseki.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.web.HttpOp;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.stereotype.Repository;

import com.liferay.portal.kernel.util.PropsUtil;

import de.seitenbau.govdata.constants.FileExtension;
import de.seitenbau.govdata.constants.GovDataConfigParam;
import de.seitenbau.govdata.fuseki.FusekiClient;
import de.seitenbau.govdata.fuseki.FusekiEndpoint;
import de.seitenbau.govdata.fuseki.util.FusekiQueryTemplates;
import de.seitenbau.govdata.navigation.PortletUtil;

/**
 * Ein Client für den Zugriff auf den Apache Jena Fuseki.
 * 
 */
@Repository
public class FusekiClientImpl implements FusekiClient
{
  private static final Logger LOG = LoggerFactory.getLogger(FusekiClient.class);

  private static final String SUPPORT_ENABLED = "ENABLED";

  private static final String SUPPORT_DISABLED = "DISABLED";

  private final String fusekiDatastoreBaseEndpoint = PortletUtil.getLinkToFusekiTriplestoreUrl();

  private final String fusekiBaseUrl = PropsUtil.get(GovDataConfigParam.FUSEKI_URL);

  private boolean supportDisabled = true;

  /**
   * Zusätzliche Checks nach Initialisierung der Klasse.
   */
  @PostConstruct
  public void initalize()
  {
    if (StringUtils.isNotBlank(fusekiBaseUrl))
    {
      if (isAvailable())
      {
        supportDisabled = false;
      }
      else
      {
        throw new BeanInitializationException("A TripleStore endpoint with URL " + fusekiBaseUrl
            + " is configured, but the TripleStore is not available!");
      }
    }
    logStatus();
  }

  @Override
  public void deleteDataset(String ckanDatasetBaseUrl, String ckanTechnicalId, String identifier)
  {
    final String method = "deleteDataset() : ";
    LOG.trace("{}Start. ckanTechnicalId: {}, Identifier: {}.", method, ckanTechnicalId, identifier);

    if (supportDisabled)
    {
      logStatus(method);
      LOG.trace(method + "End");
      return;
    }

    Model model = loadModelFromCkanRdf(ckanDatasetBaseUrl, ckanTechnicalId);
    String datasetUri = getUriFromModel(model, identifier);
    deleteDataset(datasetUri, identifier);

    LOG.trace(method + "End");
  }

  private void deleteDataset(String datasetUri, String identifier)
  {
    final String method = "deleteDataset() : ";
    LOG.trace("{}Start. datasetUri: {}, Identifier: {}.", method, datasetUri, identifier);

    if (StringUtils.isBlank(datasetUri))
    {
      LOG.debug("{} End. No dataset found with identifier: {}."
          + " Skip deleting dataset from triplestore.", method, identifier);
      return;
    }

    try (RDFConnection conn = RDFConnectionFactory.connect(getUpdateEndpoint()))
    {
      conn.begin(ReadWrite.WRITE);
      try
      {
        // delete everything from the dataset except for the organization
        LOG.debug("Deleting dataset with URIRef: " + datasetUri);
        UpdateRequest updateQuery =
            UpdateFactory.create(FusekiQueryTemplates.getDeleteDatasetQuery(datasetUri));
        conn.update(updateQuery);
        conn.commit();
        LOG.debug("Deleting dataset was successful");
      }
      catch (Exception e)
      {
        conn.abort();
        LOG.error("Problem with sparql request: " + e.getMessage());
      }
      finally
      {
        conn.end();
      }
    }
    catch (Exception ex)
    {
      LOG.error("{}Problem with rdf connection: ", method, ex);
    }
    LOG.trace(method + "End");
  }

  @Override
  public void updateOrCreateDataset(String ckanDatasetBaseUrl, String ckanTechnicalId, String identifier)
  {
    final String method = "updateOrCreateDataset() : ";
    LOG.trace(method + "Start");

    if (supportDisabled)
    {
      logStatus(method);
      LOG.trace(method + "End");
      return;
    }

    Model model = loadModelFromCkanRdf(ckanDatasetBaseUrl, ckanTechnicalId);
    String datsetUri = getUriFromModel(model, identifier);
    // delete existing dataset if possible before creating it
    deleteDataset(datsetUri, identifier);
    createInTriplestore(model);

    LOG.trace(method + "End");
  }

  private void createInTriplestore(Model model)
  {
    final String method = "createInTriplestore() : ";
    LOG.trace(method + "Start");

    try (RDFConnection conn = RDFConnectionFactory.connect(getDataEndpoint()))
    {
      conn.begin(ReadWrite.WRITE);
      try
      {
        LOG.debug("Creating new dataset in the triplestore");
        conn.load(model);
        conn.commit();
        LOG.debug("New dataset was created successfully");
      }
      catch (Exception e)
      {
        conn.abort();
        LOG.error("Problem with sparql request: " + e.getMessage());
      }
      finally
      {
        conn.end();
      }
    }
    catch (Exception ex)
    {
      LOG.error("{}Problem with rdf connection.", method, ex);
    }
    LOG.trace(method + "End");
  }

  private Model loadModelFromCkanRdf(String ckanDatasetBaseUrl, String ckanTechnicalId)
  {
    final String method = "loadModelFromCkanRdf() : ";
    LOG.trace(method + "Start");

    Model result = null;
    try
    {
      String uri = ckanDatasetBaseUrl + ckanTechnicalId + FileExtension.RDF;
      LOG.debug("Load Apache Jena RDF model from uri {}", uri);
      result = RDFDataMgr.loadModel(uri); // read rdf from ckan-uri and load it as model
    }
    catch (Exception ex)
    {
      LOG.error("{}Unexpected error while loading RDF model!", method, ex);
    }
    return result;
  }

  private String getUriFromModel(Model model, String identifier)
  {
    final String method = "getUriFromModel() : ";
    LOG.trace(method + "Start");

    String result = null;
    if (model != null)
    {
      try
      {
        LOG.debug("Get URI for dataset with identifier {}", identifier);
        ResIterator itR = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
        while (itR.hasNext())
        {
          Resource r = itR.next();
          result = r.getURI(); // should never find more than one result
          if (itR.hasNext())
          {
            LOG.warn("{}Found more than one resource with predicate RDF.type and object DCAT.Dataset! "
                + "Dataset with identifier {}", method, identifier);
          }
          break;
        }
      }
      catch (Exception ex)
      {
        LOG.error("{}Unexpected error while reading URI from model!", method, ex);
      }
    }

    LOG.trace(method + "End");
    return result;
  }

  private void logStatus()
  {
    logStatus(null);
  }

  private void logStatus(String method)
  {
    if (method != null)
    {
      LOG.debug("{}TripleStore support is {}!", method, getSupportText());
    }
    else
    {
      LOG.info("TripleStore support is {}!", getSupportText());
    }
  }

  private String getSupportText()
  {
    String result = SUPPORT_DISABLED;
    if (!supportDisabled)
    {
      result = SUPPORT_ENABLED;
    }
    return result;
  }

  private boolean isAvailable()
  {
    boolean result = false;
    try
    {
      String response = HttpOp.execHttpGetString(FusekiEndpoint.PING.getFusekiEndpoint(fusekiBaseUrl));
      if (response != null)
      {
        result = true;
      }
    }
    catch (Exception e)
    {
      LOG.error("Error while checking availabilty of the TripleStore!", e);
    }
    return result;
  }

  private String getUpdateEndpoint()
  {
    return FusekiEndpoint.UPDATE.getFusekiEndpoint(fusekiDatastoreBaseEndpoint);
  }

  private String getDataEndpoint()
  {
    return FusekiEndpoint.DATA.getFusekiEndpoint(fusekiDatastoreBaseEndpoint);
  }

}
