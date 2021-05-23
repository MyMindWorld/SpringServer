package io.scriptServer.controller;

import io.scriptServer.model.Role;
import io.scriptServer.model.User;
import io.scriptServer.repository.*;
import io.scriptServer.service.*;
import io.scriptServer.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);
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

    @Autowired
    UserService userService;
    @Autowired
    EmailingService emailingService;

    @RequestMapping(value = "/admin/invite_user", method = RequestMethod.POST)
    public ModelAndView sendInvite(User user, @RequestParam("roleVar") List<Long> rolesRaw, Model model, HttpServletRequest request) {

        user.setUsername(user.getUsername().trim());

        Iterable<Long> iterable = rolesRaw;

        List<Role> roles = roleRepository.findAllById(iterable);

        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());

        if (userFromRepo != null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "User already exists! If you want to update roles, please refer to Update User Roles page.");
            return users(model);
        }


        logger.info("Received add user request from : '" + Utils.getUsername() + "' adding " + user);
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "User add", user.toString());

        user.setEmail(user.getUsername() + "@script_server.io");
        user.setLdapName(user.getUsername());
        String newPassword = utils.generateSecurePassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setRoles(roles);
        emailingService.sendInviteUserEmail(user, request);

        userRepository.save(user); // prop register method

        model.addAttribute("success", true);
        model.addAttribute("successMessage", "User '" + user.getUsername() + "' is successfully created!");


        return users(model);
    }


    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    public ModelAndView users(Model model) {

        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("privileges", privilegeRepository.findAll());
        model.addAttribute("users", userRepository.findAll());


        return new ModelAndView("users");
    }


    @RequestMapping(value = "/admin/update_user_roles", method = RequestMethod.POST)
    public ModelAndView updateUserPost(User user, @RequestParam("roleVarUpdate") List<Long> rolesRaw, Model model, HttpServletRequest request) {
        logger.info("Received role update from : '" + Utils.getUsername() + "' updating user " + user.getUsername());
        Iterable<Long> iterable = rolesRaw;
        List<Role> roles = roleRepository.findAllById(iterable);
        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());

        if (userFromRepo == null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "User with username '" + user.getUsername() + "' not found!!! Please contact admin");
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "User Delete", user.toString(), "USER NOT FOUND!");
        } else {
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "User role update", userFromRepo.toString() + " New roles : " + roles.toString());
            userFromRepo.setRoles(roles);
            userRepository.save(userFromRepo);
            model.addAttribute("success", true);
            model.addAttribute("successMessage", "Updated user '" + userFromRepo.getUsername() + "' roles successfully!");
        }
        return users(model);
    }

    @RequestMapping(value = "/admin/delete_user", method = RequestMethod.POST)
    public ModelAndView deleteUser(User user, Model model, HttpServletRequest request) {
        logger.info("Received user delete from : '" + Utils.getUsername() + "' deleting user " + user.getUsername());
        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());

        if (userFromRepo == null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "User with username '" + user.getUsername() + "' not found!!! Please contact admin");
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "User Delete", user.toString(), "USER NOT FOUND!");

        } else {
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Delete user", userFromRepo.toString());
            userRepository.delete(userFromRepo);
            model.addAttribute("success", true);
            model.addAttribute("successMessage", "Deleted user '" + userFromRepo.getUsername() + "' successfully!");
        }
        return users(model);
    }

}
