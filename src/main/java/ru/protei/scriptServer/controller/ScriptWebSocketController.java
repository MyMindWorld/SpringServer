package ru.protei.scriptServer.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.protei.scriptServer.model.POJO.Message;
import ru.protei.scriptServer.model.POJO.OutputMessage;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.UserRepository;

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
    private UserRepository userRepository;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

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

    public void sendToSock(String username, Message message) {
        logger.info("SENDING MESSAGE sendToSock OBJ " + message.getText());
        message.setTime(new SimpleDateFormat("HH:mm").format(new Date()));
        this.simpMessagingTemplate.convertAndSendToUser(username,"/reply", message);
    }

    public void sendToSock(String username, String message) {
        Message messageObj = new Message();
        messageObj.setFrom("SCRIPT");
        messageObj.setText(message);
        messageObj.setTime(new SimpleDateFormat("HH:mm").format(new Date()));
        logger.info("SENDING MESSAGE sendToSock STRING " + message);
        this.simpMessagingTemplate.convertAndSendToUser(username,"/reply", messageObj);
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages/")
    public OutputMessage sendReceivedMessageToWS(Message message) {
        // Cюда приходят сообщения от юзеров, отсюда их можно передавать в скрипты.
        // В данный момент возвращаются всем обратно
        logger.info("SENDING MESSAGE sendReceivedMessageToWS  " + message.getText());
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }

}
