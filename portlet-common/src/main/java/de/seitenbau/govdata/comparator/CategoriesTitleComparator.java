package de.seitenbau.govdata.comparator;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import de.seitenbau.govdata.odp.registry.model.Category;

/**
 * The Class CategoriesTitleComparator.
 */
public class CategoriesTitleComparator implements Comparator<Category>, Serializable
{
  private static final long serialVersionUID = 8313501201700864633L;

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Category c1, Category c2)
  {
    Collator collator = Collator.getInstance(Locale.GERMAN);
    collator.setStrength(Collator.SECONDARY); // a == A, a < Ä
    return collator.compare(c1.getTitle(), c2.getTitle());
  }

}
