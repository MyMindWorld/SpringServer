package ru.protei.scriptServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.naming.ldap.LdapName;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Users")
@Data
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", Username='" + Username + '\'' +
                ", ldapName='" + ldapName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}