package ru.protei.scriptServer.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
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

    @Override
    public String toString() {
        return "LogEntity{" +
                "triggeredBy='" + triggeredBy + '\'' +
                ", date=" + date +
                ", ip='" + ip + '\'' +
                ", action='" + action + '\'' +
                ", errorLog='" + errorLog + '\'' +
                '}';
    }

}
