package ru.protei.scriptServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String ldapName;
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

    public UserDetails getUserDetails() {
        return new org.springframework.security.core.userdetails.User(
                this.getUsername(), this.getPassword(), this.isEnabled(), true, true,
                true, this.getAuthorities());
    }

    public List<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().flatMap(role -> role.getAuthorities().stream()).collect(Collectors.toList());
    }

}