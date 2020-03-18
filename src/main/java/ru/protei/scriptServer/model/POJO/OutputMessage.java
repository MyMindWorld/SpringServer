package ru.protei.scriptServer.model.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputMessage {
    private String from;
    private String text;
    private String time;
}
