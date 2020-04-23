package ru.protei.scriptServer.model.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.protei.scriptServer.model.Enums.ModalType;
import ru.protei.scriptServer.model.Enums.ServiceMessage;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String username;
    private String scriptName;
    private String addressedTo;
    private ModalType modalType;
    private String text;
    private String time;
    private String uniqueSessionId;
    private ServiceMessage serviceMessage;


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