package ru.protei.scriptServer.model.POJO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunningScript {

    String sessionId;
    String scriptName;
    String userName;
    String threadName;
}
