package ru.protei.scriptServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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


    public Script() {
        super();
    }

    public String toString(){
        return id + "_" + Name;
    }
}
