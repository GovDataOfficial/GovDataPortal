package de.seitenbau.govdata.permission;

public enum LiferayRole
{
  /**
   * Liferay role Redakteur
   */
  REDAKTEUR("Redakteur"),

  /**
   * Liferay role Chefredakteur
   */
  CHEFREDAKTEUR("Chefredakteur"),

  /**
   * Liferay role Geschaeftsstelle
   */
  GESCHAEFTSSTELLE("Geschaeftsstelle"),

  /**
   * Liferay role Administrator
   */
  ADMINISTRATOR("Administrator");

  private String roleName;

  private LiferayRole(String roleName)
  {
    this.roleName = roleName;
  }

  public String getRoleName()
  {
    return this.roleName;
  }
}
