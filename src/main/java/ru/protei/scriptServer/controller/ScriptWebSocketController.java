package ru.protei.scriptServer.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.protei.scriptServer.config.MessageQueueConfig;
import ru.protei.scriptServer.model.Enums.ModalType;
import ru.protei.scriptServer.model.Enums.ServiceMessage;
import ru.protei.scriptServer.model.POJO.Message;

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
    // TODO refactor to one method
    public void sendToSock(Message message, String uniqueSessionId) {
        message.setUniqueSessionId(uniqueSessionId);
        logger.info("Sending message to socket : " + message.toString() + " and sessionId " + uniqueSessionId);
        this.simpMessagingTemplate.convertAndSendToUser(message.getAddressedTo(), "/reply/" + message.getScriptName(), message, createHeaders(uniqueSessionId));
    }

    public void sendToSockFromServerService(String addressedTo, String message, String scriptName, String uniqueSessionId, ServiceMessage serviceMessage) {
        Message messageObj = new Message();
        messageObj.setUsername("SERVER");
        messageObj.setServiceMessage(serviceMessage);
        messageObj.setText(message);
        messageObj.setScriptName(scriptName);
        messageObj.setAddressedTo(addressedTo);
        messageObj.setTime(new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()));
        sendToSock(messageObj,uniqueSessionId);
    }

    public void sendToSockFromServer(String addressedTo, String message, String scriptName, String uniqueSessionId) {
        Message messageObj = new Message();
        messageObj.setUsername("SERVER");
        messageObj.setText(message);
        messageObj.setScriptName(scriptName);
        messageObj.setAddressedTo(addressedTo);
        messageObj.setTime(new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()));
        sendToSock(messageObj, uniqueSessionId);
    }

    public void sendToSockFromServer(String addressedTo, String message, String scriptName, ModalType modalType, String uniqueSessionId) {
        Message messageObj = new Message();
        messageObj.setModalType(modalType);
        messageObj.setUsername("SERVER");
        messageObj.setText(message);
        messageObj.setScriptName(scriptName);
        messageObj.setAddressedTo(addressedTo);
        messageObj.setTime(new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()));
        sendToSock(messageObj, uniqueSessionId);
    }

    public void sendToSockFromScript(String addressedTo, String message, String scriptName, String uniqueSessionId) {
        Message messageObj = new Message();
        messageObj.setUsername("SCRIPT");
        messageObj.setText(message);
        messageObj.setScriptName(scriptName);
        messageObj.setAddressedTo(addressedTo);
        messageObj.setTime(new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()));
        sendToSock(messageObj, uniqueSessionId);
    }

    public void sendToSockFromUser(Message message) {
        message.setTime(new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()));
        logger.info("SENDING MESSAGE sendToSock STRING " + message);
        this.simpMessagingTemplate.convertAndSendToUser(message.getUsername(), "/reply/" + message.getScriptName(), message, createHeaders(message.getUniqueSessionId()));
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    @MessageMapping("/scriptsSocket")
    public void sendReceivedMessageToWS(Message message) {
        // here message from user is received
        logger.info("SENDING MESSAGE sendReceivedMessageToWS  " + message.getText());
        queueConfig.blockingQueue().add(message);
        sendToSockFromUser(message);
    }

}
