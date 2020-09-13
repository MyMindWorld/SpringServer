package ru.protei.scriptServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.LogEntity;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.LogRepository;
import ru.protei.scriptServer.repository.RoleRepository;
import ru.protei.scriptServer.repository.UserRepository;
import ru.protei.scriptServer.utils.Utils;

import java.util.List;

@Controller
public class AdminPageController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    LogRepository logRepository;
    @Autowired
    Utils utils;


    @RequestMapping("/admin")
    public ModelAndView adminPage() {
        ModelAndView modelAndView = new ModelAndView("admin");
        List<User> userList = userRepository.findAll();
        List<Role> roleList = roleRepository.findAll();
        List<LogEntity> logEntities = logRepository.findAllByOrderByDateDesc();

        modelAndView.addObject("users", userList);
        modelAndView.addObject("roles", roleList);
        modelAndView.addObject("log", logEntities);

        return modelAndView;
    }


    @RequestMapping("/admin/server_control")
    public ModelAndView serverControlPage() {
        ModelAndView modelAndView = new ModelAndView("ServerControlPage");

        modelAndView.addObject("processMap", utils.getCopyOfProcessQueue());

        return modelAndView;
    }


}

