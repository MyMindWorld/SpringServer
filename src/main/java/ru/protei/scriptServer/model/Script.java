package ru.protei.scriptServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Scripts")
@Data
@AllArgsConstructor
public class Script {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Column(columnDefinition = "LONGTEXT")
    private String parametersJson;


    public Script() {
        super();
    }

    public String toString() {
        return "Id : '" + id + "' Name : '" + name + "'";
    }
}
