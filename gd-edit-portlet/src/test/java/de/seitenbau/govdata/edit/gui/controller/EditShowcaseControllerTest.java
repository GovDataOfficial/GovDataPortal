package de.seitenbau.govdata.edit.gui.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.liferay.portletmvc4spring.test.mock.web.portlet.MockResourceRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockResourceResponse;

import de.seitenbau.govdata.db.api.ShowcaseClientFactory;
import de.seitenbau.govdata.edit.constants.EditCommonConstants;
import de.seitenbau.govdata.testing.db.api.testdata.TestDataFactory;

@RunWith(MockitoJUnitRunner.class)
public class EditShowcaseControllerTest
{
  private TestDataFactory testDataFactory = new TestDataFactory();

  @Mock
  private ShowcaseClientFactory showcaseClientFactory;

  @InjectMocks
  private EditShowcaseController sut;

  @BeforeClass
  public static void setupClass() throws Exception
  {
    Files.createDirectories(EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET);
  }

  @Test
  public void initialize_cleanEditPortletsTempDirectory() throws Exception
  {
    /* prepare */
    Path tmpfile = Files.createTempFile(EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET, "", ".png");

    /* execute */
    sut.initialize();

    /* assert */
    Assertions.assertThat(Files.exists(tmpfile)).isFalse();
  }

  @Test
  public void serveImage_invalidPathInFileName() throws Exception
  {
    /* prepare */
    Path tmpfile = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir")), "", ".png");
    Files.write(tmpfile, testDataFactory.testImg);
    MockResourceRequest request = new MockResourceRequest();
    request.addParameter("filename", "../" + tmpfile.getFileName().toString());
    MockResourceResponse response = new MockResourceResponse();

    /* execute */
    sut.serveImage(request, response);

    /* assert */
    Assertions.assertThat(response.getContentLength()).isZero();
    Assertions.assertThat(response.getContentType()).isNull();
    Assertions.assertThat(response.getContentAsByteArray()).isEmpty();
  }

  @Test
  public void serveImage_filename_null() throws Exception
  {
    /* prepare */
    MockResourceRequest request = new MockResourceRequest();
    MockResourceResponse response = new MockResourceResponse();

    /* execute */
    sut.serveImage(request, response);

    /* assert */
    Assertions.assertThat(response.getContentLength()).isZero();
    Assertions.assertThat(response.getContentType()).isNull();
    Assertions.assertThat(response.getContentAsByteArray()).isEmpty();
  }

  @Test
  public void serveImage_emptyTempFile() throws Exception
  {
    /* prepare */
    Path tmpfile = Files.createTempFile(EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET, "", ".png");
    MockResourceRequest request = new MockResourceRequest();
    request.addParameter("filename", tmpfile.getFileName().toString());
    MockResourceResponse response = new MockResourceResponse();

    /* execute */
    sut.serveImage(request, response);

    /* assert */
    Assertions.assertThat(response.getContentLength()).isZero();
    Assertions.assertThat(response.getContentType()).isNull();
    Assertions.assertThat(response.getContentAsByteArray()).isEmpty();
  }

  @Test
  public void serveImage_valid() throws Exception
  {
    /* prepare */
    Path tmpfile = Files.createTempFile(EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET, "", ".png");
    Files.write(tmpfile, testDataFactory.testImg);
    MockResourceRequest request = new MockResourceRequest();
    request.addParameter("filename", tmpfile.getFileName().toString());
    MockResourceResponse response = new MockResourceResponse();

    /* execute */
    sut.serveImage(request, response);

    /* assert */
    Assertions.assertThat(response.getContentLength()).isBetween(476, 592);
    Assertions.assertThat(response.getContentType()).isEqualTo("image/png");
    Assertions.assertThat(response.getContentAsByteArray()).isNotEmpty();
  }
}
