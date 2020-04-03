package ru.protei.scriptServer.controller;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.scriptServer.model.Parameters;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.ScriptsHandler;
import ru.protei.scriptServer.service.UserService;
import ru.protei.scriptServer.service.VenvManager;
import ru.protei.scriptServer.utils.SystemIntegration.DynamicParamsScriptsRunner;
import ru.protei.scriptServer.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

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
    ScriptsHandler scriptsHandler;
    @Autowired
    LogService logService;
    @Autowired
    VenvManager venvManager;
    @Autowired
    ScriptWebSocketController scriptWebSocketController;
    @Autowired
    DynamicParamsScriptsRunner dynamicParamsScriptsRunner;

    @SneakyThrows
    @RequestMapping(value = "/scripts/run_script_select", method = RequestMethod.GET)
    public String runScriptForSelect(String scriptName, String paramName, HttpServletRequest req) {
        // todo return value to list on load or dynamicly? Doing this on server might me easier, but select2
        //  supoprts ajax https://select2.org/data-sources/ajax

        Script script = scriptRepository.findByNameEquals(scriptName);
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Arrays.toString(principal.getAuthorities().toArray()).contains(script.getName())) { // legshooting
            logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "RUNNING SCRIPT WITHOUT ROLE! '" + scriptName + "'", paramName);
            // todo 403 page
            return null;
        }
        Parameters[] paramsList = utils.stringToListOfParams(script.getParametersJson());
        for (Parameters param : paramsList) {
            if (param.name.equals(paramName)) {
                ArrayList<String> scriptResult = dynamicParamsScriptsRunner.run(param.getScript(), utils.getScriptsDirectory());
                return utils.createResultsSelect2Json(scriptResult);
            }
        }
        logger.info("Returning nothing");
        return null;
    }
}
