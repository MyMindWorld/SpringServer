package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.repository.UserRepository;
import ru.protei.scriptServer.utils.Utils;


import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping("/index")
    public ModelAndView  showMenu() {
        ModelAndView modelAndView = new ModelAndView("index");
        List<Script> scriptList = scriptRepository.findAll();

        modelAndView.addObject("list", scriptList);

        return modelAndView;
    }


    @RequestMapping(value = "/userdelete", method = RequestMethod.POST)
    public ModelAndView deleteUser(Model model, HttpServletRequest request) {
        List<User> userList = userRepository.findAll();
//        userList.sort(User::compareTo);
        long devId = Long.parseLong(request.getParameter("id"));
        if (devId != 0) {

            String username = getUsername();
            User userToDelete = userRepository.getOne(devId);
            logger.warn("Removing user '" + userToDelete + " by request of '" + username + "'");
            userRepository.delete(userToDelete);
            userList.remove(userToDelete);
            return new ModelAndView("userdelete").addObject("users", userList);
        }

        return new ModelAndView("userdelete").addObject("users", userList);
    }

}
