package de.seitenbau.govdata.odp.common.util;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;
import org.junit.Test;

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
    // Bytes are different between Java 8 and Java 11, so check the size, because it's easier
    Assertions.assertThat(result).hasSizeBetween(13334, 13390); // Java 8: 13334, Java 11: 13390
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
    // Bytes are different between Java 8 and Java 11, so check the size, because this is equal
    Assertions.assertThat(result).hasSize(118);
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
    // Bytes are different between Java 8 and Java 11, so check the size, because this is equal
    Assertions.assertThat(result).hasSize(70);
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
