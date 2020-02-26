package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.repository.UserRepository;
import ru.protei.scriptServer.utils.Utils;

import java.util.List;

import static ru.protei.scriptServer.utils.Utils.getUsername;

@Controller
public class AdminPageController {
    Logger logger = LoggerFactory.getLogger(ru.protei.scriptServer.controller.MainPageController.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    Utils utils;
    @Autowired
    ScriptsHandler scriptsHandler;

    @RequestMapping("/admin")
    public ModelAndView adminPage() {
        ModelAndView modelAndView = new ModelAndView("admin");
        List<User> userList = userRepository.findAll();

        modelAndView.addObject("users", userList);

        return modelAndView;
    }

    @RequestMapping(value = "/admin/invite_user", method = RequestMethod.POST)
    public String sendInvite(User user) {


        user.setEmail(user.getLdapName() + "@protei.ru");
        user.setUsername(user.getLdapName());

        logger.debug("Received add user request from : '" + getUsername() + "' adding " + user.toString());
        userRepository.save(user);
        // send invite link


        return "redirect:/admin";
    }

    @RequestMapping(value = "/admin/update_scripts", method = RequestMethod.GET)
    public String updateScripts(User user) {
        scriptsHandler.updateScriptsInDb();


        return "redirect:/admin";
    }


}

