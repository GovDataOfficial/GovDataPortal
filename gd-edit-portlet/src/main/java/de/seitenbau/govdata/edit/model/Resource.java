package de.seitenbau.govdata.edit.model;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource implements Serializable
{
  private static final long serialVersionUID = -516410984434917647L;
  
  @URL
  @NotEmpty
  private String url;
  
  @NotEmpty
  private String format;
  
  private String description;
  
  private String name;
  
  @Pattern(regexp="[a-z]{2}|", message="{languageCode}")
  private String language;
  
}
