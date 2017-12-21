package de.seitenbau.govdata.edit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * An address-container for a contact.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactAddress implements Serializable
{
  private String addressee;

  private String details;

  private String street;

  private String city;

  private String zip;

  private String country;
}
