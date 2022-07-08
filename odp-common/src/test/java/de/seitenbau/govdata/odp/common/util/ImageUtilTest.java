package de.seitenbau.govdata.odp.common.util;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import de.seitenbau.govdata.odp.common.util.ImageUtil;

public class ImageUtilTest
{
  @Test
  public void convertByteArrayToBase64String_null()
  {
    /* execute */
    String result = ImageUtil.convertByteArrayToBase64String(null);

    /* assert */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void convertByteArrayToBase64String()
  {
    /* execute */
    String result = ImageUtil.convertByteArrayToBase64String(new byte[20]);

    /* assert */
    Assertions.assertThat(result).isEqualTo("data:image/png;base64,AAAAAAAAAAAAAAAAAAAAAAAAAAA=");
  }

  @Test
  public void convertByteArrayToBase64StringThumbnail_noImage()
  {
    /* execute */
    String result = ImageUtil.convertByteArrayToBase64StringThumbnail(new byte[20]);

    /* assert */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void convertByteArrayToBase64StringThumbnail() throws Exception
  {
    /* prepare */
    Path path = Paths.get(getClass().getResource("image-file.png").toURI());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Files.copy(path, out);
    out.flush();
    out.close();

    /* execute */
    String result = ImageUtil.convertByteArrayToBase64StringThumbnail(out.toByteArray());

    /* assert */
    Path pathExpected = Paths.get(getClass().getResource("image-thumbnail-base64.txt").toURI());
    Assertions.assertThat(result).isEqualTo(FileUtils.readFileToString(pathExpected.toFile(), "utf-8"));
  }

  @Test
  public void convertByteArrayToBase64StringThumbnail_withSize() throws Exception
  {
    /* prepare */
    Path path = Paths.get(getClass().getResource("image-file.png").toURI());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Files.copy(path, out);
    out.flush();
    out.close();

    /* execute */
    String result = ImageUtil.convertByteArrayToBase64StringThumbnail(out.toByteArray(), 1, 1);

    /* assert */
    Assertions.assertThat(result).isEqualTo(
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNYv/nQfwAHXQMk"
            + "v62ELwAAAABJRU5ErkJggg==");
  }

  @Test
  public void getThumbnail_null()
  {
    /* execute */
    byte[] result = ImageUtil.getThumbnail(null);

    /* assert */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void getThumbnail_empty()
  {
    /* execute */
    byte[] result = ImageUtil.getThumbnail(new byte[] {});

    /* assert */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void getThumbnail_byteArray_noImage()
  {
    /* execute */
    byte[] result = ImageUtil.getThumbnail(new byte[20]);

    /* assert */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void getThumbnail() throws Exception
  {
    /* prepare */
    Path path = Paths.get(getClass().getResource("image-file.png").toURI());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Files.copy(path, out);
    out.flush();
    out.close();

    /* execute */
    byte[] result = ImageUtil.getThumbnail(out.toByteArray(), 1, 1);

    /* assert */
    Assertions.assertThat(result).isEqualTo(
        new byte[] {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 1, 0, 0, 0, 1, 8,
            6, 0, 0, 0, 31, 21, -60, -119, 0, 0, 0, 13, 73, 68, 65, 84, 120, -38, 99, 88, -65, -7, -48, 127,
            0, 7, 93, 3, 36, -65, -83, -124, 47, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126});
  }

  @Test
  public void isValidFilename_null()
  {
    /* execute */
    boolean result = ImageUtil.isValidFilename(null);

    /* assert */
    Assertions.assertThat(result).isFalse();
  }

  @Test
  public void isValidFilename_empty()
  {
    /* execute */
    boolean result = ImageUtil.isValidFilename("");

    /* assert */
    Assertions.assertThat(result).isFalse();
  }

  @Test
  public void isValidFilename_blank()
  {
    /* execute */
    boolean result = ImageUtil.isValidFilename("  ");

    /* assert */
    Assertions.assertThat(result).isFalse();
  }

  @Test
  public void isValidFilename_invalidPattern_point()
  {
    /* execute */
    boolean result = ImageUtil.isValidFilename(".");

    /* assert */
    Assertions.assertThat(result).isFalse();
  }

  @Test
  public void isValidFilename_invalidPattern_asterisk()
  {
    /* execute */
    boolean result = ImageUtil.isValidFilename("*");

    /* assert */
    Assertions.assertThat(result).isFalse();
  }

  @Test
  public void isValidFilename_invalidPattern_path()
  {
    /* execute */
    boolean result = ImageUtil.isValidFilename("../test.png");

    /* assert */
    Assertions.assertThat(result).isFalse();
  }

  @Test
  public void isValidFilename_valid_minimum()
  {
    /* execute */
    boolean result = ImageUtil.isValidFilename("t.t");

    /* assert */
    Assertions.assertThat(result).isTrue();
  }

  @Test
  public void isValidFilename_valid_usual()
  {
    /* execute */
    boolean result = ImageUtil.isValidFilename("4234234789.png");

    /* assert */
    Assertions.assertThat(result).isTrue();
  }
}
