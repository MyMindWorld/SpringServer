package io.scriptServer.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ToString
@Entity
@Data
public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String triggeredBy;
    @NotNull
    private Date date;
    @NotBlank
    private String ip;
    @NotBlank
    @Column(columnDefinition = "LONGTEXT")
    private String action;
    @Column(columnDefinition = "LONGTEXT")
    private String params;
    @Column(columnDefinition = "LONGTEXT")
    private String errorLog;
}
