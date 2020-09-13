package ru.protei.scriptServer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Venv")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Venv {
    @NotBlank
    @Id
    private String name;
    @ElementCollection
    private List<String> installedPackages = new ArrayList<>();

}
