package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;

import de.seitenbau.govdata.odp.registry.model.RoleEnumType;
import lombok.Data;

@Data
public class ContactBean implements Serializable
{
    private static final long serialVersionUID = 13998654793551790L;

    private RoleEnumType role;

    private String name;

    private String url;

    private String email;

    private String address;
}
