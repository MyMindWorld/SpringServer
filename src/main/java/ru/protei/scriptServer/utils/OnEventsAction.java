package ru.protei.scriptServer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import ru.protei.scriptServer.controller.ScriptsHandler;

@Controller
public class OnEventsAction {

    Logger logger = LoggerFactory.getLogger(OnEventsAction.class);
    @Autowired
    ScriptsHandler scriptsHandler;

    @EventListener(ApplicationStartedEvent.class)
    public void afterStartup() {
        logger.info("AfterStartup invocation started!");
        scriptsHandler.updateScriptsInDb();
    }
}
