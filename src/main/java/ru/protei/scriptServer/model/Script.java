package ru.protei.scriptServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Scripts")
@Data
@AllArgsConstructor
public class Script {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
    @NotBlank
    @Id
    //@Column(nullable = false, unique = true) // ????
    private String name;
    @NotBlank
    private String display_name;
    @NotBlank
    @Column(columnDefinition = "LONGTEXT")
    private String parametersJson;


    public Script() {
        super();
    }

    public String toString() {
        return "display_name : '" + display_name + "' Name : '" + name + "'";
    }
}
