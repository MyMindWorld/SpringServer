package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.POJO.Parameters;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.repository.UserRepository;
import ru.protei.scriptServer.service.StorageService;
import ru.protei.scriptServer.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.protei.scriptServer.utils.Utils.getUsername;


@Controller
public class MainPageController {
    Logger logger = LoggerFactory.getLogger(MainPageController.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    Utils utils;
    @Value("${app.version}")
    private String appVersion;
    @Autowired
    StorageService storageService;


    @RequestMapping("/")
    public ModelAndView redirectToIndex(HttpServletRequest req) {
        return showMenu(req);
    }

    @RequestMapping("/index")
    public ModelAndView showMenu(HttpServletRequest req) {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView modelAndView = new ModelAndView("index");
        String allowedScripts = Arrays.toString(loggedInUser.getAuthorities().toArray()); // legshooting
        List<Script> scriptList = scriptRepository.findAll();
        List<String> groupsList = new ArrayList<>();
        // Creating list for displaying
        for (Script script : scriptList) {
            if ((!groupsList.contains(script.getGroup_name())) & allowedScripts.contains(script.getName())) {
                groupsList.add(script.getGroup_name());
            }
        }
        modelAndView.addObject("username", req.getRemoteUser());
        modelAndView.addObject("list", scriptList);
        modelAndView.addObject("groups", groupsList);
        if (appVersion.equals("@project.version@")) {
            appVersion = getClass().getPackage().getImplementationVersion();
        }
        modelAndView.addObject("AppVersion", appVersion);

        return modelAndView;
    }


    @RequestMapping(value = "/index/{scriptName}", method = RequestMethod.GET)
    public ModelAndView showScriptContent(HttpServletRequest request, @PathVariable String scriptName) {
        Script script = scriptRepository.findByNameEquals(scriptName);
        if (script == null) {
            return new ModelAndView("ErrorCodes/404");
        }
        logger.info("User '" + getUsername() + "' requested script '" + scriptName + "', and displayName : " + script.getDisplayName());
        Parameters[] parameters = utils.stringToListOfParams(script.getParametersJson());
        ModelAndView modelAndView = showMenu(request);


        return modelAndView.addObject("script", script).addObject("parameters", parameters);
    }

}
