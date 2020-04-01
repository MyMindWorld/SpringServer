package ru.protei.scriptServer.controller;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.*;
import ru.protei.scriptServer.model.POJO.Message;
import ru.protei.scriptServer.model.POJO.OutputMessage;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.ScriptsHandler;
import ru.protei.scriptServer.service.UserService;
import ru.protei.scriptServer.service.VenvManager;
import ru.protei.scriptServer.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class ScriptsController {

    Logger logger = LoggerFactory.getLogger(ScriptsController.class);
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
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

    @RequestMapping("/admin/scripts")
    public ModelAndView scriptsPage() {
        ModelAndView modelAndView = new ModelAndView("scripts");
        List<Script> scriptList = scriptRepository.findAll();

        modelAndView.addObject("scriptsList", scriptList);

        return modelAndView;
    }

    @RequestMapping(value = "/admin/update_scripts", method = RequestMethod.GET)
    public String updateScripts(HttpServletRequest request) {
        scriptsHandler.updateAllScriptsConfigs();
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Scripts update", "");

        return "redirect:/admin/scripts";
    }

    @RequestMapping(value = "/admin/update_scripts_and_drop_venv", method = RequestMethod.GET)
    public String updateScriptsAndDropVenv(HttpServletRequest request) {
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "All Venv deleting", "");
        venvManager.deleteAllVenvs();
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Scripts update", "");
        scriptsHandler.updateAllScriptsConfigs();
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Creating default venv", "");
        venvManager.createDefaultVenv();
        return "redirect:/admin/scripts";
    }

    @RequestMapping(value = "/admin/update_scripts_from_gitlab", method = RequestMethod.GET)
    public String updateScriptsFromGitlab(HttpServletRequest request) {
        scriptsHandler.updateAllScriptsConfigs();
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Scripts update from gitlab", "");
        logger.info("Scripts update from gitlab will be available later!");

        return "redirect:/admin/scripts";
    }

    @RequestMapping(value = "/admin/update_script", method = RequestMethod.POST)
    public String updateSpecifiedScriptConfig(HttpServletRequest request,Script scriptToUpdate) {
        Script scriptFromDB = scriptRepository.findByNameEquals(scriptToUpdate.getName());
        if (scriptFromDB != null){
            scriptsHandler.updateSpecifiedScriptConfigAndDropVenv(scriptFromDB);
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Script '" + scriptFromDB.getName() + "' update", "");
        }
        else {
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Script '" + scriptFromDB.getName() + "' update", "","SCRIPT NOT FOUND IN DB!");
            logger.error("Script not found!");
        }

        return "redirect:/admin/scripts";
    }



    @SneakyThrows
    @RequestMapping(value = "/scripts/run_script", method = RequestMethod.POST)
    @ResponseBody
    public void runScript(@RequestParam Map<String, String> allRequestParams, String name, HttpServletRequest req) {
        // todo return value to list on load or dynamicly? Doing this on server might me easier, but select2
        //  supoprts ajax https://select2.org/data-sources/ajax

        Script scriptObject = scriptRepository.findByNameEquals(name);
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Script script = scriptRepository.findByNameEquals(scriptObject.getName());
        Message message = new Message();
        if (!Arrays.toString(principal.getAuthorities().toArray()).contains(script.getName())) { // legshooting
            logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "RUNNING SCRIPT WITHOUT ROLE! '" + script.getName() + "'", String.valueOf(allRequestParams));
            // todo 403 page
            return;
        }
        for (String value : allRequestParams.keySet()
        ) {
            logger.info(value + " : " + allRequestParams.get(value));

        }
        if (allRequestParams.size() == 0) {
            message.setFrom("SCRIPT");
            message.setText("Parameters could not be empty! Or should they...");
            sendToSock(message);
        }
        if (script.getVenv() == null) {
            message.setFrom("SCRIPT");
            message.setText("Using default venv. It's HIGHLY recommended not doing this. Please, specify unique venv name and requirements file");
            sendToSock(message);
            if (script.getRequirements()!= null){
                message.setFrom("SCRIPT");
                message.setText("Adding requirements to default venv is forbidden! Please use custom venv for this case!");
                sendToSock(message);
            }
        }

        message.setFrom("SCRIPT");
        message.setText("Started!");
        sendToSock(message);


        String[] resultRunString = utils.createParamsString(script, allRequestParams);

        logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "Run script '" + script.getName() + "'", Arrays.toString(resultRunString));
        logger.info("Created string : " + Arrays.toString(resultRunString));
//
        scriptsHandler.runPythonScript(resultRunString, script);

//        return "redirect:" + req.getHeader("Referer");
    }

    public void sendToSock(Message message) {
        logger.info("SENDING MESSAGE sendToSock OBJ " + message.getText());
        message.setTime(new SimpleDateFormat("HH:mm").format(new Date()));
        this.simpMessagingTemplate.convertAndSend("/topic/messages/", message);
    }

    public void sendToSock(String message) {
        Message messageObj = new Message();
        messageObj.setFrom("SCRIPT");
        messageObj.setText(message);
        messageObj.setTime(new SimpleDateFormat("HH:mm").format(new Date()));
        logger.info("SENDING MESSAGE sendToSock STRING " + message);
        this.simpMessagingTemplate.convertAndSend("/topic/messages/", messageObj);
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages/")
    public OutputMessage sendReceivedMessageToWS(Message message) {
        logger.info("SENDING MESSAGE sendReceivedMessageToWS  " + message.getText());
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }
}
