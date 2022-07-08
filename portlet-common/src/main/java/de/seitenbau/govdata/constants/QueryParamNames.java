package de.seitenbau.govdata.constants;

/**
 * Enthält die Konstanten mit den Parameternamen der Suche und Detailansicht.
 * 
 * @author tscheffler
 * @author rnoerenberg
 *
 */
public abstract class QueryParamNames
{
  /** Der Parametername für den Suchbegriff der einfachen Suche. */
  public static final String PARAM_PHRASE = "q";

  /** Der Parametername für die Filter der Suche. */
  public static final String PARAM_FILTER = "f";

  /** Der Parametername für die Scroll-ID der Suche. */
  public static final String PARAM_SCROLL_ID = "scrollId";

  /** Der Parametername für die Sortierung der Suche. */
  public static final String PARAM_SORT = "s";

  /** Der Parametername für das Rating der Suche. */
  public static final String PARAM_RATING = "rating";

  /** Der Parametername für den Metadatennamen. */
  public static final String PARAM_DATASET = "dataset";

  /** Der Parametername für einen Kommentar. */
  public static final String PARAM_COMMENT = "comment";

  /** Der Parametername für die E-Mail-Adresse. */
  public static final String PARAM_EMAIL_ADDRESS = "emailAddress";

  /** Der Parametername für die Kommentar-ID. */
  public static final String PARAM_COMMENT_ID = "commentId";

  /** Der Parametername für den Inhalt eines Kommentars. */
  public static final String PARAM_MESSAGE = "message";

  /** Der Parametername für die Koordinaten der Suche. */
  public static final String PARAM_BOUNDINGBOX = "boundingbox";

  /** Der Parametername für den Start für den Zeitbezug in der Suche. */
  public static final String PARAM_START = "start";

  /** Der Parametername für das Ende für den Zeitbezug in der Suche. */
  public static final String PARAM_END = "end";

  /** Der Parametername für den Start für die Checkbox zum Zeitbezug in der Suche. */
  public static final String PARAM_FROM_CHECKBOX = "filter-date-from-checkbox";

  /** Der Parametername für das Ende für die Checkbox zum Zeitbezug in der Suche. */
  public static final String PARAM_UNTIL_CHECKBOX = "filter-date-until-checkbox";

  /** Der Parametername für die Suchbegriff-Vorschläge in der Suche. */
  public static final String PARAM_COMPLETION_SUGGESTIONS = "suggestions";

  /** Der Parametername für die Anzeige der eigenen Datensätze in der Suche. */
  public static final String PARAM_SHOW_ONLY_EDITOR_METADATA = "onlyEditorMetadata";

  /** Der Parametername für die Anzeige der privaten Showcases in der Suche. */
  public static final String PARAM_SHOW_ONLY_PRIVATE_SHOWCASES = "onlyPrivateShowcases";
}
