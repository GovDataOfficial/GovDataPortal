package de.seitenbau.govdata.search.adapter;

import javax.annotation.PostConstruct;
import javax.naming.ConfigurationException;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class SearchServiceElasticsearchImplDummy extends SearchServiceElasticsearchImpl
{
  @Override
  @PostConstruct
  public void init() throws ConfigurationException
  {
    // do nothing
  }
}
