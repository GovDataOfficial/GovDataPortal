package de.seitenbau.govdata.search.index;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.journal.model.JournalArticle;

/**
 * Enthält Konstanten für den Portal-Index.
 * 
 * @author rnoerenberg
 *
 */
public abstract class IndexConstants
{
  /**
   * Der Index-Typ für das Portal.
   */
  public static final String INDEX_TYPE_PORTAL = "portal";

  /** Der Index-Typ für die Showcases. */
  public static final String INDEX_TYPE_SHOWCASE = "showcase";

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

  /** The field "primary_showcase_type". */
  public static final String METADATA_FIELD_PRIMARY_SHOWCASE_TYPE = "primary_showcase_type";

  /** The field "private". */
  public static final String METADATA_FIELD_PRIVATE = "private";

  /** The field "showcase_created". */
  public static final String METADATA_FIELD_SHOWCASE_CREATED = "showcase_created";

  /** The field "showcase_modified". */
  public static final String METADATA_FIELD_SHOWCASE_MODIFIED = "showcase_modified";

  /** The field "modified_fallback_showcase_modified". */
  public static final String METADATA_FIELD_MODIFIED_FALLBACK_SHOWCASE =
      "modified_fallback_showcase_modified";

  /** The field "modified". */
  public static final String METADATA_FIELD_MODIFIED = "modified";

  /** The field "issued". */
  public static final String METADATA_FIELD_ISSUED = "issued";

  /** The field "type". */
  public static final String METADATA_FIELD_TYPE = "type";

  /** The field "groups". */
  public static final String METADATA_FIELD_GROUPS = "groups";

  /** The field "platforms". */
  public static final String METADATA_FIELD_PLATFORMS = "platforms";

  /** The field "platforms". */
  public static final String METADATA_FIELD_DATASETS = "used_datasets";

  /** The field "showcase_types". */
  public static final String METADATA_FIELD_SHOWCASE_TYPES = "showcase_types";

  /** The field "image". */
  public static final String METADATA_FIELD_IMAGE = "image";

  /** The field "spatial". */
  public static final String METADATA_FIELD_SPATIAL = "spatial";

  /** The field "boundingbox". */
  public static final String METADATA_FIELD_BOUNDING_BOX = "boundingbox";

  /** The field "spatial_area". */
  public static final String METADATA_FIELD_SPATIAL_AREA = "spatial_area";

  /** The field "spatial_center". */
  public static final String METADATA_FIELD_SPATIAL_CENTER = "spatial_center";
}
