package ru.protei.scriptServer.controller;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.*;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.ScriptsService;
import ru.protei.scriptServer.service.UserService;
import ru.protei.scriptServer.service.VenvManager;
import ru.protei.scriptServer.utils.SystemIntegration.DynamicParamsScriptsRunner;
import ru.protei.scriptServer.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class ScriptsController {

    Logger logger = LoggerFactory.getLogger(ScriptsController.class);
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
    VenvManager venvManager;
    @Autowired
    ScriptWebSocketController scriptWebSocketController;
    @Autowired
    DynamicParamsScriptsRunner dynamicParamsScriptsRunner;

    @RequestMapping("/admin/scripts")
    public ModelAndView scriptsPage() {
        ModelAndView modelAndView = new ModelAndView("scripts");
        List<Script> scriptList = scriptRepository.findAll();

        modelAndView.addObject("scriptsList", scriptList);

        return modelAndView;
    }

    @RequestMapping(value = "/admin/update_scripts", method = RequestMethod.GET)
    public String updateScripts(HttpServletRequest request) {
        scriptsService.updateAllScriptsConfigs();
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Scripts update", "");

        return "redirect:/admin/scripts";
    }

    @RequestMapping(value = "/admin/update_scripts_and_drop_venv", method = RequestMethod.GET)
    public String updateScriptsAndDropVenv(HttpServletRequest request) {
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "All Venv deleting", "");
        venvManager.deleteAllVenvs();
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Scripts update", "");
        scriptsService.updateAllScriptsConfigs();
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Creating default venv", "");
        venvManager.createDefaultVenv();
        return "redirect:/admin/scripts";
    }

    @RequestMapping(value = "/admin/update_scripts_from_gitlab", method = RequestMethod.GET)
    public String updateScriptsFromGitlab(HttpServletRequest request) {
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Scripts update from gitlab", "");
        scriptsService.getScriptsFromGit();
        scriptsService.updateAllScriptsConfigs();
        return "redirect:/admin/scripts";
    }

    @RequestMapping(value = "/admin/update_script", method = RequestMethod.POST)
    public String updateSpecifiedScriptConfig(HttpServletRequest request, Script scriptToUpdate) {
        Script scriptFromDB = scriptRepository.findByNameEquals(scriptToUpdate.getName());
        if (scriptFromDB != null) {
            scriptsService.updateSpecifiedScriptConfigAndDropVenv(scriptFromDB);
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Script '" + scriptFromDB.getName() + "' update", "");
        } else {
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Script '" + scriptToUpdate.getName() + "' update", "", "SCRIPT NOT FOUND IN DB!");
            logger.error("Script not found!");
        }

        return "redirect:/admin/scripts";
    }


    @SneakyThrows
    @RequestMapping(value = "/scripts/run_script", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity runScript(@RequestHeader(name = "sessionId")String uniqueSessionId, String scriptName, HttpServletRequest req, HttpServletResponse response) {
        Map<String, String[]> allRequestParams = req.getParameterMap();
        Script script = scriptRepository.findByNameEquals(scriptName);
        if (script == null) {
            logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "Running unknown script! '" + scriptName + "'", String.valueOf(allRequestParams));
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Arrays.toString(principal.getAuthorities().toArray()).contains(script.getName())) { // legshooting
            logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "RUNNING SCRIPT WITHOUT ROLE! '" + script.getName() + "'", String.valueOf(allRequestParams));
            // todo 403 page
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        for (String value : allRequestParams.keySet()
        ) {

            String joinedString = String.join("; ", allRequestParams.get(value));
            // TODO research if '[1,2,3]' will be more readable by python
            logger.info(value + " : " + joinedString);

        }
        if (allRequestParams.size() == 0) {
            scriptWebSocketController.sendToSockFromServer(principal.getUsername(), "Parameters could not be empty! Or should they...", script.getName(),uniqueSessionId);
        }
        if (script.getVenv() == null) {
            scriptWebSocketController.sendToSockFromServer(principal.getUsername(), "Using default venv. It's HIGHLY recommended not doing this. Please, specify unique venv name and requirements file", script.getName(),uniqueSessionId);
            if (script.getRequirements() != null) {
                scriptWebSocketController.sendToSockFromServer(principal.getUsername(), "Adding requirements to default venv is forbidden! Please use custom venv for this case!", script.getName(),uniqueSessionId);
            }
        }

        String[] resultRunString = utils.createParamsString(script, allRequestParams,req);
        logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "Run script '" + script.getName() + "'", Arrays.toString(resultRunString));
        logger.info("Created string : " + Arrays.toString(resultRunString));
        new Thread(() -> {
            scriptsService.runPythonScript(resultRunString, script, principal.getUsername(), uniqueSessionId);
        }, uniqueSessionId + "_" + script.getName() + "-" + req.getRemoteUser()).start();
        return new ResponseEntity(HttpStatus.OK);
    }

    @SneakyThrows
    @RequestMapping(value = "/scripts/kill_script", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity killScript(@RequestHeader(name = "sessionId")String uniqueSessionId,String scriptName, HttpServletRequest req) {
        Map<String, String[]> allRequestParams = req.getParameterMap();
        Script script = scriptRepository.findByNameEquals(scriptName);
        if (script == null) {
            logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "Killing unknown script! '" + scriptName + "'", String.valueOf(allRequestParams));
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Arrays.toString(principal.getAuthorities().toArray()).contains(script.getName())) { // legshooting
            logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "KILLING SCRIPT WITHOUT ROLE! '" + script.getName() + "'", String.valueOf(allRequestParams));
            // todo 403 page
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        String expectedThreadName = uniqueSessionId + "_" + script.getName() + "-" + req.getRemoteUser();
        logger.info("Getting all threads");
        Set<Thread> setOfThread = Thread.getAllStackTraces().keySet();
        for(Thread thread : setOfThread){
            if(thread.getName().equals(expectedThreadName)){
                logger.info("Thread to kill was found");
                thread.interrupt();
                thread.interrupt();
                thread.interrupt();
                thread.interrupt();
                thread.interrupt();
                while (thread.isAlive()){
                    Thread.sleep(1000);
                    logger.info("Killing still going...");
                }
                logger.info("Thread was killed!");
                return new ResponseEntity(HttpStatus.OK);
            }
        }
        logger.error("Thread '" + expectedThreadName + "' was not found!");
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);


    }

}
