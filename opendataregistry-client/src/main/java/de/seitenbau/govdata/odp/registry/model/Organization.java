package de.seitenbau.govdata.odp.registry.model;

import java.util.List;

public interface Organization extends Comparable<Organization>
{
  /**
   * Gets the ID.
   * 
   * @return the ID
   */
  String getId();

  /**
   * Gets the name.
   * 
   * @return the name
   */
  String getName();

  /**
   * Gets the display name.
   * 
   * @return the display name
   */
  String getDisplayName();

  /**
   * Gets the title.
   * 
   * @return the title
   */
  String getTitle();

  /**
   * Gets the contributorIDs.
   * 
   * @return the contributorIDs
   */
  List<String> getContributorIds();
}
