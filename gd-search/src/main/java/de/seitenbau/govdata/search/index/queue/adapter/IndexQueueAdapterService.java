package de.seitenbau.govdata.search.index.queue.adapter;

import java.util.List;

import de.seitenbau.serviceportal.common.api.RestUserMetadata;
import de.seitenbau.serviceportal.common.messaging.SearchIndexEntry;
import de.seitenbau.serviceportal.common.messaging.SearchIndexZustaendigkeit;
import de.seitenbau.serviceportal.common.messaging.UpdateAdmincenterObjectMessage;

public interface IndexQueueAdapterService
{

  void save(
      RestUserMetadata userMetadata,
      SearchIndexEntry... entry);

  void update(
      RestUserMetadata userMetadata,
      String id,
      SearchIndexEntry... entry);

  void delete(
      RestUserMetadata userMetadata,
      String id,
      SearchIndexEntry... entry);

  void updateTags(RestUserMetadata userMetadata,
      String indexType,
      Long referenceId,
      List<Long> tags);

  void updateZustaendigkeiten(Long leistungId,
      List<SearchIndexZustaendigkeit> zustaendigkeiten);

  void updateAdmincenterObject(RestUserMetadata userMetadata, Long objectId, String objectType,
      UpdateAdmincenterObjectMessage message);

  void deleteAdmincenterObject(RestUserMetadata userMetadata, Long objectId, String objectType);

  void updateSynonyms(RestUserMetadata userMetadata, String indexName, List<String> synonymTerms);

}
