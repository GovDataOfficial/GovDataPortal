package de.seitenbau.govdata.search.index.queue.adapter;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.Form;

import de.seitenbau.serviceportal.common.api.RestUserMetadata;
import de.seitenbau.serviceportal.common.messaging.SearchIndexEntry;
import de.seitenbau.serviceportal.common.messaging.SearchIndexZustaendigkeit;
import de.seitenbau.serviceportal.common.messaging.UpdateAdmincenterObjectMessage;

@Path("/index-queue")
@Produces("application/json")
@Consumes("application/json")
public interface IndexQueueAdapterServiceRESTResource extends IndexQueueAdapterService
{
  @POST
  @Path("/")
  @Override
  void save(
      @Form RestUserMetadata userMetadata,
      SearchIndexEntry... entry);

  @PUT
  @Path("/{id}")
  @Override
  void update(
      @Form RestUserMetadata userMetadata,
      @PathParam("id") String id,
      SearchIndexEntry... entry);

  @DELETE
  @Path("/{id}")
  @Override
  void delete(
      @Form RestUserMetadata userMetadata,
      @PathParam("id") String id,
      SearchIndexEntry... entry);

  @PUT
  @Path("/{id}/tags")
  @Override
  public void updateTags(@Form RestUserMetadata userMetadata, 
      @PathParam("indexType") String indexType,
      @PathParam("referenceId") Long referenceId,
      @PathParam("tags") List<Long> tags);
  
  @PUT
  @Path("/{objectType}/{objectId}/name")
  @Override
  public void updateAdmincenterObject(@Form RestUserMetadata userMetadata, @PathParam("objectId") Long objectId,
      @PathParam("objectType") String objectType,
      UpdateAdmincenterObjectMessage updateAdmincenterObjectMessage);
  
  @Override
  @PUT
  @Path("/leistung/zustaendigkeiten/{leistungId}")
  public void updateZustaendigkeiten(@PathParam("leistungId") Long leistungId, List<SearchIndexZustaendigkeit> zustaendigkeiten);
  
  @DELETE
  @Path("/{objectType}/{objectId}")
  @Override
  public void deleteAdmincenterObject(@Form RestUserMetadata userMetadata, @PathParam("objectId") Long objectId,
      @PathParam("objectType") String objectType);
  
  @PUT
  @Path("/synonyms/{indexname}")
  @Override
  public void updateSynonyms(@Form RestUserMetadata userMetadata, @PathParam("indexname") String indexName, List<String> synonymTerms);
}
