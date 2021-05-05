package de.seitenbau.govdata.search.index;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.journal.model.JournalArticle;

/**
 * Enthält Konstanten für den Portal-Index.
 * 
 * @author rnoerenberg
 *
 */
public abstract class PortalIndexConstants
{
  /**
   * Der Index-Typ für das Portal.
   */
  public static final String INDEX_TYPE_PORTAL = "portal";
  
  /**
   * Der Wert für den Mandanten im Portal-Index.
   */
  public static final String INDEX_MANDANT = "1";

  /**
   * Der Name der Klasse {@link BlogsEntry}, einschließlich Packagenamen.
   */
  public static final String CLASS_NAME_BLOGS_ENTRY = BlogsEntry.class.getName();

  /**
   * Der Name der Klasse {@link JournalArticle}, einschließlich Packagenamen.
   */
  public static final String CLASS_NAME_JOURNAL_ARTICLE_ENTRY = JournalArticle.class.getName();
}
