package io.scriptServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collection;


@Data
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    @ToString.Exclude
    private Collection<Role> roles;

    public Privilege(String name) {
        this.setName(name);
    }

}