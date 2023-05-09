package de.seitenbau.govdata.search.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.SyndPersonImpl;

import de.seitenbau.govdata.odp.common.util.GovDataCollectionUtils;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.search.gui.model.HitViewModel;

public class AtomFeedView extends AbstractAtomFeedView
{

  private static final String LINK_TO_DETAILS_TITLE = "Show in Govdata Portal";

  private static final String FEED_AUTHOR = "Govdata.de";

  private static final String FEED_EMAIL = "info@govdata.de";

  private List<HitViewModel> entries;

  private PortletURL selfurl;

  public AtomFeedView(List<HitViewModel> entries, PortletURL selfurl)
  {
    this.entries = GovDataCollectionUtils.getCopyOfList(entries);
    this.selfurl = selfurl;

  }

  @Override
  protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request)
  {
    feed.setTitle("GovData Suchergebnisse");
    feed.setUpdated(new Date()); // now
    feed.setId(selfurl.toString());
    feed.setOtherLinks(Arrays.asList(createLink(selfurl.toString(), "self", null)));

    // set Author
    SyndPerson author = new SyndPersonImpl();
    author.setEmail(FEED_EMAIL);
    author.setName(FEED_AUTHOR);
    List<SyndPerson> authors = new ArrayList<>();
    authors.add(author);
    feed.setAuthors(authors);
  }

  @Override
  protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest request,
      HttpServletResponse response) throws Exception
  {
    List<Entry> feedEntries = new ArrayList<>();

    for (HitViewModel hit : entries)
    {
      Entry entry = new Entry();

      entry.setTitle(hit.getTitle());
      entry.setId(hit.getLinkToDetailPage());

      // Set Description
      Content content = new Content();
      content.setValue(hit.getContent());
      content.setType("text/html");
      entry.setContents(Arrays.asList(content));

      // Set Date
      entry.setModified(hit.getLastModified());

      // Set Author if available
      if (StringUtils.isNotEmpty(hit.getContact()))
      {
        SyndPerson author = new SyndPersonImpl();
        author.setName(hit.getContact());

        // add Email if available
        if (StringUtils.isNotEmpty(hit.getContactEmail()))
        {
          author.setEmail(hit.getContactEmail());
        }

        List<SyndPerson> authors = new ArrayList<>();
        authors.add(author);
        entry.setAuthors(authors);
      }

      // Set Link to detail page
      entry.setAlternateLinks(
          Arrays.asList(createLink(hit.getLinkToDetailPage(), null, LINK_TO_DETAILS_TITLE)));

      // Set other links (ckan as source)
      List<Link> otherLinks = new ArrayList<>();
      if (hit.getLinkToCKan() != null)
      {
        otherLinks.add(createLink(hit.getLinkToCKan(), "via", null));
      }
      entry.setOtherLinks(otherLinks);

      // Set Categories
      List<com.rometools.rome.feed.atom.Category> categories = new ArrayList<>();
      for (Category cat : hit.getCategories())
      {
        categories.add(createCategory(cat.getName(), cat.getDisplayName()));
      }
      entry.setCategories(categories);

      feedEntries.add(entry);
    }

    return feedEntries;

  }

  private Link createLink(String url, String rel, String title)
  {
    Link link = new Link();
    link.setHref(url);
    if (rel != null)
    {
      link.setRel(rel);
    }
    if (title != null)
    {
      link.setTitle(title);
    }
    return link;
  }

  private com.rometools.rome.feed.atom.Category createCategory(String term, String label)
  {
    com.rometools.rome.feed.atom.Category category = new com.rometools.rome.feed.atom.Category();
    category.setTerm(term);
    category.setLabel(label);
    return category;
  }

}
