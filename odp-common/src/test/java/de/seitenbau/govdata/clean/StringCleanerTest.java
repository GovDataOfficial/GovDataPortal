package de.seitenbau.govdata.clean;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Tests für die Klasse {@link StringCleaner}.
 * 
 * @author rnoerenberg
 *
 */
public class StringCleanerTest
{
  @Test
  public void trimAndFilterString_null() throws Exception
  {
    /* prepare */

    /* execute */
    String result = StringCleaner.trimAndFilterString(null);

    /* verify */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void trimAndFilterString_empty() throws Exception
  {
    /* prepare */

    /* execute */
    String result = StringCleaner.trimAndFilterString("");

    /* verify */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void trimAndFilterString_blank() throws Exception
  {
    /* prepare */
    String blank = "  ";

    /* execute */
    String result = StringCleaner.trimAndFilterString(blank);

    /* verify */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void trimAndFilterString_tag_not_closed_1() throws Exception
  {
    /* prepare */
    String dirty = "test <b>gdfgfdg fdf";

    /* execute */
    String result = StringCleaner.trimAndFilterString(dirty);

    /* verify */
    Assertions.assertThat(result).isEqualTo("test gdfgfdg fdf");
  }

  @Test
  public void trimAndFilterString_square_bracket_not_closed() throws Exception
  {
    /* prepare */
    String dirty = "test <gdfgfdg fdf";

    /* execute */
    String result = StringCleaner.trimAndFilterString(dirty);

    /* verify */
    Assertions.assertThat(result).isEqualTo("test");
  }

  @Test
  public void trimAndFilterStringWithWhitelist_value_and_whitelist_null() throws Exception
  {
    /* prepare */

    /* execute */
    String result = StringCleaner.trimAndFilterString(null, null);

    /* verify */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void trimAndFilterStringWithWhitelist_value_null() throws Exception
  {
    /* prepare */

    /* execute */
    String result = StringCleaner.trimAndFilterString(null, StringCleaner.WHITELIST_METADATA_NOTES);

    /* verify */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void trimAndFilterStringWithWhitelist_value_blank() throws Exception
  {
    /* prepare */

    /* execute */
    String result = StringCleaner.trimAndFilterString("  ", StringCleaner.WHITELIST_METADATA_NOTES);

    /* verify */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void trimAndFilterStringWithWhitelist_whitelist_null() throws Exception
  {
    /* prepare */
    String dirty = "test <b>bold</b>";

    /* execute */
    String result = StringCleaner.trimAndFilterString(dirty, null);

    /* verify */
    Assertions.assertThat(result).isEqualTo(dirty);
  }

  @Test
  public void trimAndFilterStringWithWhitelist() throws Exception
  {
    /* prepare */
    String dirty =
        "test <b>bold</b> <p>paragraph <b>bold</b></p> <ul><li>one</li></ul> <ol><li>two</li></ol>";

    /* execute */
    String result = StringCleaner.trimAndFilterString(dirty, StringCleaner.WHITELIST_METADATA_NOTES);

    /* verify */
    Assertions.assertThat(result).isEqualTo("test bold \n<p>paragraph bold</p> "
        + "\n<ul>\n <li>one</li>\n</ul> \n<ol>\n <li>two</li>\n</ol>");
  }

  @Test
  public void trimAndFilterStringWithWhitelist_aTag_href_empty() throws Exception
  {
    /* prepare */
    String dirty = "test <a href=\"\" >link</a>";

    /* execute */
    String result = StringCleaner.trimAndFilterString(dirty, StringCleaner.WHITELIST_METADATA_NOTES);

    /* verify */
    Assertions.assertThat(result).isEqualTo("test \n<a rel=\"nofollow\" target=\"_blank\">link</a>");
  }

  @Test
  public void trimAndFilterStringWithWhitelist_aTag_invalid_attributes() throws Exception
  {
    /* prepare */
    String dirty = "test <a href=\"http://test.de\" title=\"title\" alt=\"alt\" style=\"style\" >link</a>";

    /* execute */
    String result = StringCleaner.trimAndFilterString(dirty, StringCleaner.WHITELIST_METADATA_NOTES);

    /* verify */
    Assertions.assertThat(result).isEqualTo(
        "test \n<a href=\"http://test.de\" rel=\"nofollow\" target=\"_blank\">link</a>");
  }

  @Test
  public void trimAndFilterStringWithWhitelist_aTag_href_ftp() throws Exception
  {
    /* prepare */
    String dirty = "test <a href=\"ftp://test.de\" >link</a>";

    /* execute */
    String result = StringCleaner.trimAndFilterString(dirty, StringCleaner.WHITELIST_METADATA_NOTES);

    /* verify */
    Assertions.assertThat(result).isEqualTo(
        "test \n<a href=\"ftp://test.de\" rel=\"nofollow\" target=\"_blank\">link</a>");
  }

  @Test
  public void trimAndFilterStringWithWhitelist_aTag_href_http() throws Exception
  {
    /* prepare */
    String dirty = "test <a href=\"http://test.de\" >link</a>";

    /* execute */
    String result = StringCleaner.trimAndFilterString(dirty, StringCleaner.WHITELIST_METADATA_NOTES);

    /* verify */
    Assertions.assertThat(result).isEqualTo(
        "test \n<a href=\"http://test.de\" rel=\"nofollow\" target=\"_blank\">link</a>");
  }

  @Test
  public void trimAndFilterStringWithWhitelist_aTag_href_https() throws Exception
  {
    /* prepare */
    String dirty = "test <a href=\"https://test.de\" >link</a>";

    /* execute */
    String result = StringCleaner.trimAndFilterString(dirty, StringCleaner.WHITELIST_METADATA_NOTES);

    /* verify */
    Assertions.assertThat(result).isEqualTo(
        "test \n<a href=\"https://test.de\" rel=\"nofollow\" target=\"_blank\">link</a>");
  }

  @Test
  public void trimAndFilterStringWithWhitelist_aTag_href_mailto() throws Exception
  {
    /* prepare */
    String dirty = "test <a href=\"mailto://test@test.de\" >link</a>";

    /* execute */
    String result = StringCleaner.trimAndFilterString(dirty, StringCleaner.WHITELIST_METADATA_NOTES);

    /* verify */
    Assertions.assertThat(result).isEqualTo(
        "test \n<a href=\"mailto://test@test.de\" rel=\"nofollow\" target=\"_blank\">link</a>");
  }

  @Test
  public void trimAndFilterStringWithWhitelist_aTag_enforced_attributes() throws Exception
  {
    /* prepare */
    String dirty = "test <a href=\"http://test.de\" rel=\"follow\" target=\"_self\" >link</a>";

    /* execute */
    String result = StringCleaner.trimAndFilterString(dirty, StringCleaner.WHITELIST_METADATA_NOTES);

    /* verify */
    Assertions.assertThat(result).isEqualTo(
        "test \n<a href=\"http://test.de\" rel=\"nofollow\" target=\"_blank\">link</a>");
  }

  @Test
  public void mungeTitleToName_max() throws Exception
  {
    /* prepare */
    String title = "BöÄüß +;á Titel  -  ";
    title = StringUtils.rightPad(title, 120, "A");

    /* execute */
    String result = StringCleaner.mungeTitleToName(title);

    /* verify */
    Assertions.assertThat(result).isEqualTo(StringUtils.rightPad("boau-a-titel-", 100, "a"));
  }

  @Test
  public void mungeTitleToName_min() throws Exception
  {
    /* prepare */
    String title = "Ä";

    /* execute */
    String result = StringCleaner.mungeTitleToName(title);

    /* verify */
    Assertions.assertThat(result).isEqualTo(StringUtils.rightPad("a", 2, "_"));
  }
}
