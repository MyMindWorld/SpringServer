package ru.protei.scriptServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Date;
import java.util.Collection;

@Entity
@Table(name = "Users")
@Data
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String ldapName; // replace with LdapName?
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private boolean enabled = true;
    private Date lastLogin;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;


    public User() {
        super();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", Username='" + username + '\'' +
                ", ldapName='" + ldapName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}