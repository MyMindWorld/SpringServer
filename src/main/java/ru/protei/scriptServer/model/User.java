package ru.protei.scriptServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.naming.ldap.LdapName;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Users")
@Data
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String Username;
    @NotBlank
    private String ldapName; // replace with LdapName?
    @NotBlank
    private String email;

    public User() {
        super();
    }

}