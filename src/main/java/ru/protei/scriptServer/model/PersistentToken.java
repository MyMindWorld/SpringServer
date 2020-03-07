package ru.protei.scriptServer.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "persistent_logins")
@Data
public class PersistentToken {
    @NotBlank
    private String username;
    @Id
    private String series;
    @NotNull
    private String token;
    @NotNull
    private Timestamp last_used;
}
