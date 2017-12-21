package de.seitenbau.govdata.edit.model;

import java.io.Serializable;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.URL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact implements Serializable
{
  private static final long serialVersionUID = -1253636402396326173L;

  private String Name;
  
  @URL
  private String url;
  
  @Email
  private String email;
  
  private ContactAddress address;
  
}
