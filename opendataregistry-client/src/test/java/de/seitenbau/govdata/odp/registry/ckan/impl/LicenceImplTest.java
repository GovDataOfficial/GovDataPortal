package de.seitenbau.govdata.odp.registry.ckan.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.model.LicenceConformance;

@RunWith(MockitoJUnitRunner.class)
public class LicenceImplTest
{
  @Test
  public void open() throws Exception
  {
    /* prepare */
    LicenceBean bean = createLicense(Licence.OPEN);

    /* execute */
    LicenceImpl sut = new LicenceImpl(bean);

    /* verify */
    assertThat(sut.isOpen()).isTrue();
    assertThat(sut.isActive()).isTrue();
  }

  @Test
  public void closed() throws Exception
  {
    /* prepare */
    LicenceBean bean = createLicense(Licence.CLOSED);

    /* execute */
    LicenceImpl sut = new LicenceImpl(bean);

    /* verify */
    assertThat(sut.isOpen()).isFalse();
    assertThat(sut.isActive()).isTrue();
  }

  @Test
  public void empty() throws Exception
  {
    /* prepare */
    LicenceBean bean = new LicenceBean();

    /* execute */
    LicenceImpl sut = new LicenceImpl(bean);

    /* verify */
    assertThat(sut.isOpen()).isFalse();
    assertThat(sut.isActive()).isFalse();
  }

  @Test(expected = NullPointerException.class)
  public void read_null() throws Exception
  {
    /* execute */
    LicenceImpl sut = new LicenceImpl(null);

    /* verify */
    sut.getTitle();
  }

  private LicenceBean createLicense(Licence licence)
  {
    LicenceBean licenceBean = new LicenceBean();
    licenceBean.setId(licence.id);
    licenceBean.setStatus("active");
    licenceBean.setOd_conformance(licence.conformance.getValue());
    return licenceBean;
  }

  private enum Licence
  {
    OPEN("http://dcat-ap.de/def/licenses/dl-by-de/2.0", LicenceConformance.APPROVED),

    CLOSED("http://dcat-ap.de/def/licenses/other-closed", LicenceConformance.NOTREVIEWED);

    String id;

    LicenceConformance conformance;

    Licence(String url, LicenceConformance conformance)
    {
      this.id = url;
      this.conformance = conformance;
    }
  }
}
