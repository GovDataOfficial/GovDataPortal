package de.seitenbau.govdata.permission;

public enum LiferayRole
{
  REDAKTEUR("Redakteur"),

  CHEFREDAKTEUR("Chefredakteur"),

  GESCHAEFTSSTELLE("Geschaeftsstelle"),

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
