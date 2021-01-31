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


@Data
@Entity
@Builder
@Table(name = "Venv")
@NoArgsConstructor
@AllArgsConstructor
public class Venv {
    @NotBlank
    @Id
    private String name;
    @ElementCollection
    private List<String> installedPackages = new ArrayList<>();

}
