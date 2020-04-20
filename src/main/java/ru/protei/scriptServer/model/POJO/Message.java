package ru.protei.scriptServer.model.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String username;
    private String scriptName;
    private String addressedTo;
    private String text;
    private String time;

    @Override
    public String toString() {
        return "Message{" +
                "username='" + username + '\'' +
                ", scriptName='" + scriptName + '\'' +
                ", text='" + text + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}