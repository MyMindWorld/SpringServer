package ru.protei.scriptServer.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Collection;
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
    private String action;
    private String params;

    private String errorLog;

    @Override
    public String toString() {
        return "LogEntity{" +
                "id=" + id +
                ", triggeredBy='" + triggeredBy + '\'' +
                ", date=" + date +
                ", ip='" + ip + '\'' +
                ", action='" + action + '\'' +
                ", errorLog='" + errorLog + '\'' +
                '}';
    }

}
