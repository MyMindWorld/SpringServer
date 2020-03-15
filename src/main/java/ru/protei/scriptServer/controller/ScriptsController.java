package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.protei.scriptServer.repository.*;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.ScriptsHandler;
import ru.protei.scriptServer.service.UserService;
import ru.protei.scriptServer.utils.Utils;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ScriptsController {

    Logger logger = LoggerFactory.getLogger(AdminPageController.class);
    @Autowired
    UserService userService;
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    Utils utils;
    @Autowired
    ScriptsHandler scriptsHandler;
    @Autowired
    LogService logService;

    @RequestMapping(value = "/scripts/run_script", method = RequestMethod.GET)
    public String runScript(@RequestParam String[] commandParams, HttpServletRequest request) {
        logger.info("Received params : ");
        for (String param : commandParams) {
            logger.info(param);
        }

        scriptsHandler.runPythonScript(commandParams);


        return "redirect:" + request.getHeader("Referer");
    }
}
