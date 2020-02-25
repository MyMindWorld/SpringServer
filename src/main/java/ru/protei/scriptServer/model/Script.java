package ru.protei.scriptServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "Scripts")
@Data
@AllArgsConstructor
public class Script {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String Name;
    @NotBlank
    @Column(columnDefinition = "LONGTEXT")
    private String ParametersJson;


    public Script() {
        super();
    }

    public String toString() {
        return "Id : '" + id + "' Name : '" + Name + "'";
    }
}
