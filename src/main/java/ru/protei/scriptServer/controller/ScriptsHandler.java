package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import ru.protei.scriptServer.model.JsonScript;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

@Controller
public class ScriptsHandler {
    Logger logger = LoggerFactory.getLogger(ScriptsHandler.class);
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    Utils utils;


    public void updateScriptsInBd() {
        logger.info("Database cleanup started!");

        if (scriptRepository.findAll().size() > 0) {
            scriptRepository.deleteAll();
            logger.info("Database is now clean");
        }

        Resource[] ScriptsPath = utils.getScriptsPath();
        if (ScriptsPath == null) {
            logger.warn("Scripts folder is empty!");
            return;
        }

        for (Resource resource : ScriptsPath) {

            Script script = new Script();
            try {
                script.setName(utils.parseJsonToObject(resource.getInputStream()).name);
                JsonScript jsonScript = utils.parseJsonToObject(resource.getInputStream());
            } catch (IOException e) {
                logger.error("Mapping json to object failed!", e);
            }
            scriptRepository.save(script);

        }

        logger.warn(scriptRepository.findAll().toString());


    }
}
