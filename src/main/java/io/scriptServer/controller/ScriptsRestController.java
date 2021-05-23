package io.scriptServer.controller;

import io.scriptServer.model.User;
import io.scriptServer.repository.ScriptRepository;
import io.scriptServer.service.LogService;
import io.scriptServer.service.ScriptsService;
import io.scriptServer.service.UserService;
import io.scriptServer.service.VenvService;
import io.scriptServer.utils.Utils;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.scriptServer.model.POJO.Parameters;
import io.scriptServer.model.Script;
import io.scriptServer.utils.SystemIntegration.DynamicParamsScriptsRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class ScriptsRestController {
    Logger logger = LoggerFactory.getLogger(ScriptsRestController.class);
    @Autowired
    UserService userService;
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    Utils utils;
    @Autowired
    ScriptsService scriptsService;
    @Autowired
    LogService logService;
    @Autowired
    VenvService venvService;
    @Autowired
    ScriptWebSocketController scriptWebSocketController;
    @Autowired
    DynamicParamsScriptsRunner dynamicParamsScriptsRunner;

    @SneakyThrows
    @RequestMapping(value = "/scripts/run_script_select", method = RequestMethod.GET)
    public String runScriptForSelect(String scriptName, String paramName, String search, String formData, HttpServletRequest req) {
        if (search == null) {
            search = "";
        }
        logger.info("scriptName '" + scriptName + "'");
        logger.info("paramName '" + paramName + "'");
        logger.info("search '" + search + "'");
        logger.info("formData '" + formData + "'");
        Map<String, List<String>> formQuery = utils.splitQuery(formData);
        for (String value : formQuery.keySet()
        ) {
            logger.info(value + " : " + formQuery.get(value));

        }
        Script script = scriptRepository.findByNameEquals(scriptName);
        User user = userService.getUserByName(req.getRemoteUser());
        if (!userService.checkPrivilege(user, scriptName)) {
            logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "RUNNING SCRIPT WITHOUT ROLE! '" + scriptName + "'", paramName);
            // todo 403 page
            return null;
        }
        Parameters[] paramsList = utils.stringToListOfParams(script.getParametersJson());
        for (Parameters param : paramsList) {
            if (param.name.equals(paramName)) {
                ArrayList<String> scriptResult = dynamicParamsScriptsRunner.run(utils.buildSelectQueryRun(param.getScript(), search, formQuery), utils.getScriptsDirectory());
                return utils.createResultsSelect2Json(scriptResult, param, search);
            }
        }
        logger.info("Returning nothing");
        return null;
    }
}
