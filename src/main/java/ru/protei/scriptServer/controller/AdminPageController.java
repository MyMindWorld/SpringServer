package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.LogEntity;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.*;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.RoleService;
import ru.protei.scriptServer.service.ScriptsService;
import ru.protei.scriptServer.utils.Utils;

import java.util.List;


@Controller
public class AdminPageController {
    Logger logger = LoggerFactory.getLogger(AdminPageController.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    LogRepository logRepository;
    @Autowired
    RoleService roleService;
    @Autowired
    PrivilegeRepository privilegeRepository;
    @Autowired
    Utils utils;
    @Autowired
    ScriptsService scriptsService;
    @Autowired
    LogService logService;
    @Autowired
    PasswordEncoder passwordEncoder;


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

