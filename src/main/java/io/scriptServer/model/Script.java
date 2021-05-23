package io.scriptServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

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
    private String displayName;
    @NotBlank
    private String python_version;
    private String venv;
    private String requirements;
    @NotBlank
    private String script_path;
    @NotBlank
    @Column(columnDefinition = "LONGTEXT")
    private String parametersJson;


    public Script() {
        super();
    }

    public String toString() {
        return "display_name : '" + displayName + "' Name : '" + name + "'";
    }
}
