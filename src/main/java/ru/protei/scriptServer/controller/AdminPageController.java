package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.LogEntity;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.*;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.RoleService;
import ru.protei.scriptServer.service.ScriptsHandler;
import ru.protei.scriptServer.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

import static ru.protei.scriptServer.utils.Utils.getUsername;

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
    ScriptsHandler scriptsHandler;
    @Autowired
    LogService logService;
    @Autowired
    PasswordEncoder passwordEncoder;

    // todo Метод, который меняет все роли использующие это название скрипта на новые - для миграции названия
    // todo Фикс пустого вфбора чекбоксов в модалках (Js валидация)
    // todo GET-POST-GET в редиректах, чтобы можно было нажать назад?

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

    @RequestMapping(value = "/admin/invite_user", method = RequestMethod.POST)
    public ModelAndView sendInvite(User user, @RequestParam("roleVar") List<Long> rolesRaw, Model model,HttpServletRequest request) {

        user.setUsername(user.getUsername().trim());

        Iterable<Long> iterable = rolesRaw;

        List<Role> roles = roleRepository.findAllById(iterable);

        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());

        if (userFromRepo!=null){
            model.addAttribute("error",true);
            model.addAttribute("errorMessage","User already exists! If you want to update roles, please refer to Update User Roles page.");
            return createUser(model);
        }



        logger.info("Received add user request from : '" + getUsername() + "' adding " + user.toString());
        logService.logAction(request.getRemoteUser(),request.getRemoteAddr(),"User add",user.toString());

        user.setEmail(user.getUsername() + "@protei.ru");
        user.setLdapName(user.getUsername());
//        String newPassword = utils.generateSecurePassword(); // Do not forget to bring this baby back)
        String newPassword = user.getUsername();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setRoles(roles);
        // send invite link

        logger.info("password for " + user.getUsername() + " is set to '" + newPassword + "'"); // obv needs to be deleted
        userRepository.save(user); // prop register method

        model.addAttribute("success",true);
        model.addAttribute("successMessage","User '" + user.getUsername() + "' is successfully created!");


        return createUser(model);
    }

    @RequestMapping(value = "/admin/update_scripts", method = RequestMethod.GET)
    public String updateScripts(HttpServletRequest request) {
        logService.logAction(request.getRemoteUser(),request.getRemoteAddr(),"SCRIPTS UPDATE","");

        return "redirect:/admin";
    }

    @RequestMapping(value = "/admin/create_role", method = RequestMethod.GET)
    public String roles(Model model) {

        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("privileges", privilegeRepository.findAll());


        return "create_role";
    }

    @RequestMapping(value = "/admin/create_role", method = RequestMethod.POST)
    public String createRole(@ModelAttribute("name") String roleName, @RequestParam("privileges") List<Long> privileges_raw, Model model,HttpServletRequest request) {
        roleName = roleName.trim();


        Iterable<Long> iterable = privileges_raw;

        List<Privilege> privileges = privilegeRepository.findAllById(iterable);


        logger.info("Received create role request from : '" + getUsername() + "' adding " + roleName);
        logService.logAction(request.getRemoteUser(),request.getRemoteAddr(),"Role add",roleName);

        Role createdRole = roleService.createRoleIfNotFound(roleName, privileges);
        if (createdRole != null) {
            logger.info("Role created");
            model.addAttribute("success",true);
            model.addAttribute("successMessage","Role '" + roleName + "' is successfully created!");
        } else {
            logger.warn("Role with this name exists!");
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Role '" + roleName + "' is already exists! Updating roles is not a thing here.");
        }


        return roles(model);
    }

    @RequestMapping(value = "/admin/create_user", method = RequestMethod.GET)
    public ModelAndView createUser(Model model) {

        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("privileges", privilegeRepository.findAll());
        model.addAttribute("users", userRepository.findAll());


        return new ModelAndView("create_user");
    }

    @RequestMapping(value = "/admin/update_user", method = RequestMethod.GET)
    public ModelAndView updateUser(Model model) {

        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("privileges", privilegeRepository.findAll());
        model.addAttribute("users", userRepository.findAll());


        return new ModelAndView("update_user");
    }

    @RequestMapping(value = "/admin/update_user", method = RequestMethod.POST)
    public ModelAndView updateUserPost(User user, @RequestParam("roleVar") List<Long> rolesRaw,Model model,HttpServletRequest request) {

        logger.info("Received role update from : '" + getUsername() + "' updating user " + user.toString());
        logService.logAction(request.getRemoteUser(),request.getRemoteAddr(),"User role update",user.toString());
        // todo log update to which roles

        Iterable<Long> iterable = rolesRaw;

        List<Role> roles = roleRepository.findAllById(iterable);

        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());

        if (userFromRepo!=null){
            logger.info("Received role update from : '" + getUsername() + "' updating user " + user.toString());
            userFromRepo.setRoles(roles);
            userRepository.save(userFromRepo);
            model.addAttribute("success",true);
            model.addAttribute("successMessage","Updated user roles successfully!");
            return updateUser(model);
        }

        model.addAttribute("error",true);
        model.addAttribute("errorMessage","User with username '" + user.getUsername() + "' not found!!! Please contact admin");
        logService.logAction(request.getRemoteUser(),request.getRemoteAddr(),"User add",user.toString(),"USER NOT FOUND!");

        return updateUser(model);
    }


}

