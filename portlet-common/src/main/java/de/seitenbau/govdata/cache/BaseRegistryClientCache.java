package de.seitenbau.govdata.cache;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import de.seitenbau.govdata.odr.RegistryClient;

/**
 * Eine Basisklasse mit gemeinsam genutzten Methoden für die verschiedenen Cache-Implementierungen
 * mit Zugriff auf CKAN.
 * 
 * @author rnoerenberg
 *
 */
@Getter
@Setter
public abstract class BaseRegistryClientCache extends BaseCache
{
  @Inject
  private RegistryClient registryClient;

}
