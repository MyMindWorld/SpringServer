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
    @NotBlank
    @Id
    private String name;
    @Column(columnDefinition = "LONGTEXT")
    private String group_name;
    @NotBlank
    private String display_name;
    @NotBlank
    private String script_path;
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
