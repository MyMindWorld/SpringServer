package ru.protei.scriptServer.controller;

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
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.*;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.RoleService;
import ru.protei.scriptServer.service.ScriptsService;
import ru.protei.scriptServer.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.scriptServer.utils.Utils.getUsername;

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


        logger.info("Received add user request from : '" + getUsername() + "' adding " + user.toString());
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "User add", user.toString());

        user.setEmail(user.getUsername() + "@protei.ru");
        user.setLdapName(user.getUsername());
//        String newPassword = utils.generateSecurePassword(); // Do not forget to bring this baby back)
        String newPassword = user.getUsername();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setRoles(roles);
        // send invite link

        logger.info("password for " + user.getUsername() + " is set to '" + newPassword + "'"); // obv needs to be deleted
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


    @RequestMapping(value = "/admin/update_user", method = RequestMethod.POST)
    public ModelAndView updateUserPost(User user, @RequestParam("roleVarUpdate") List<Long> rolesRaw, Model model, HttpServletRequest request) {
        logger.info("Received role update from : '" + getUsername() + "' updating user " + user.getUsername());
        Iterable<Long> iterable = rolesRaw;
        List<Role> roles = roleRepository.findAllById(iterable);
        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());

        if (userFromRepo != null) {
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "User role update", userFromRepo.toString() + " New roles : " + roles.toString());
            userFromRepo.setRoles(roles);
            userRepository.save(userFromRepo);
            model.addAttribute("success", true);
            model.addAttribute("successMessage", "Updated user '" + userFromRepo.getUsername() + "' roles successfully!");
        } else {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "User with username '" + user.getUsername() + "' not found!!! Please contact admin");
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "User Delete", user.toString(), "USER NOT FOUND!");

        }
        return users(model);
    }

    @RequestMapping(value = "/admin/delete_user", method = RequestMethod.POST)
    public ModelAndView deleteUser(User user, Model model, HttpServletRequest request) {

        logger.info("Received user delete from : '" + getUsername() + "' deleting user " + user.getUsername());
        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());

        if (userFromRepo != null) {
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Delete user", userFromRepo.toString());
            userRepository.delete(userFromRepo);
            model.addAttribute("success", true);
            model.addAttribute("successMessage", "Deleted user '" + userFromRepo.getUsername() + "' successfully!");
        } else {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "User with username '" + user.getUsername() + "' not found!!! Please contact admin");
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "User Delete", user.toString(), "USER NOT FOUND!");

        }
        return users(model);
    }

}
