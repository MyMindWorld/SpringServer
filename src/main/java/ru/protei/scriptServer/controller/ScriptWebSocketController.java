package ru.protei.scriptServer.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.protei.scriptServer.config.MessageQueueConfig;
import ru.protei.scriptServer.model.POJO.Message;
import ru.protei.scriptServer.model.POJO.OutputMessage;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
public class ScriptWebSocketController {
    Logger logger = LoggerFactory.getLogger(ScriptWebSocketController.class);
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    MessageQueueConfig queueConfig;

    private Gson gson = new Gson();

    @MessageMapping("/message")
    @SendToUser("/queue/reply")
    public String processMessageFromClient(
            @Payload String message,
            Principal principal) throws Exception {
        return gson
                .fromJson(message, Map.class)
                .get("name").toString();
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

    public void sendToSock(Message message) {
        logger.info("Sending message to socket : " + message.toString());
        this.simpMessagingTemplate.convertAndSendToUser(message.getAddressedTo(), "/reply/" + message.getScriptName(), message);
    }

    public void sendToSockFromServer(String addressedTo, String message, String scriptName) {
        Message messageObj = new Message();
        messageObj.setUsername("SERVER");
        messageObj.setText(message);
        messageObj.setScriptName(scriptName);
        messageObj.setAddressedTo(addressedTo);
        messageObj.setTime(new SimpleDateFormat("HH:mm").format(new Date()));
        sendToSock(messageObj);
    }

    public void sendToSockFromScript(String addressedTo, String message, String scriptName) {
        Message messageObj = new Message();
        messageObj.setUsername("SCRIPT");
        messageObj.setText(message);
        messageObj.setScriptName(scriptName);
        messageObj.setAddressedTo(addressedTo);
        messageObj.setTime(new SimpleDateFormat("HH:mm").format(new Date()));
        sendToSock(messageObj);
    }

    public void sendToSockFromUser(Message message) {
        message.setTime(new SimpleDateFormat("HH:mm").format(new Date()));
        logger.info("SENDING MESSAGE sendToSock STRING " + message);
        this.simpMessagingTemplate.convertAndSendToUser(message.getUsername(), "/reply/" + message.getScriptName(), message);
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages/")
    public void sendReceivedMessageToWS(Message message) {
        // here is message from user is received
        logger.info("Received message from user : '" + message.getUsername() + "'");
        logger.info("SENDING MESSAGE sendReceivedMessageToWS  " + message.getText());
        queueConfig.blockingQueue().add(message);
        sendToSockFromUser(message);
    }

}
