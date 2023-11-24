package de.seitenbau.govdata.comparator;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import de.seitenbau.govdata.odp.registry.model.Licence;

/**
 * The Class LicencesTitleComparator.
 */
public class LicencesTitleComparator implements Comparator<Licence>, Serializable
{
  private static final long serialVersionUID = 1919490600029177942L;

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Licence licence1, Licence licence2)
  {
    Collator collator = Collator.getInstance(Locale.GERMAN);
    collator.setStrength(Collator.SECONDARY); // a == A, a < Ä
    return collator.compare(licence1.getTitle(), licence2.getTitle());
  }

}
