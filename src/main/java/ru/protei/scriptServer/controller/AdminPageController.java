package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.PrivilegeRepository;
import ru.protei.scriptServer.repository.RoleRepository;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.repository.UserRepository;
import ru.protei.scriptServer.service.RoleService;
import ru.protei.scriptServer.service.ScriptsHandler;
import ru.protei.scriptServer.utils.Utils;

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
    RoleService roleService;
    @Autowired
    PrivilegeRepository privilegeRepository;
    @Autowired
    Utils utils;
    @Autowired
    ScriptsHandler scriptsHandler;
    @Autowired
    PasswordEncoder passwordEncoder;

    // todo Метод, который меняет все роли использующие это название скрипта на новые - для миграции названия


    @RequestMapping("/admin")
    public ModelAndView adminPage() {
        ModelAndView modelAndView = new ModelAndView("admin");
        List<User> userList = userRepository.findAll();
        List<Role> roleList = roleRepository.findAll();

        modelAndView.addObject("users", userList);
        modelAndView.addObject("roles", roleList);

        return modelAndView;
    }

    @RequestMapping(value = "/admin/invite_user", method = RequestMethod.POST)
    public String sendInvite(User user,@RequestParam("roleVar") List<Long> rolesRaw) {

        Iterable<Long> iterable = rolesRaw;

        List<Role> roles = roleRepository.findAllById(iterable);

        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());

        if (userFromRepo!=null){
            logger.info("Received role update from : '" + getUsername() + "' updating user " + user.toString());
            userFromRepo.setRoles(roles);
            userRepository.save(userFromRepo);
            return "redirect:/admin";
        }



        logger.info("Received add user request from : '" + getUsername() + "' adding " + user.toString());

        user.setEmail(user.getUsername() + "@protei.ru");
        user.setLdapName(user.getUsername());
        String newPassword = utils.generateSecurePassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setRoles(roles);
        // send invite link

        logger.info("password is set to :" + newPassword); // obv needs to be deleted
        userRepository.save(user); // prop register method


        return "redirect:/admin";
    }

    @RequestMapping(value = "/admin/update_scripts", method = RequestMethod.GET)
    public String updateScripts(User user) {
        scriptsHandler.updateScriptsInDb();


        return "redirect:/admin";
    }

    @RequestMapping(value = "/admin/roles", method = RequestMethod.GET)
    public String roles(Model model) {

        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("privileges", privilegeRepository.findAll());


        return "roles";
    }

    @RequestMapping(value = "/admin/create_role", method = RequestMethod.POST)
    public String createRole(@ModelAttribute("name") String roleName, @RequestParam("privileges") List<Long> privileges_raw) {


        Iterable<Long> iterable = privileges_raw;

        List<Privilege> privileges = privilegeRepository.findAllById(iterable);


        logger.info("Received create role request from : '" + getUsername() + "' adding " + roleName);

        Role createdRole = roleService.createRoleIfNotFound(roleName, privileges);
        if (createdRole != null) {
            logger.info("Role created");
        } else
            logger.warn("Role with this name exists!");
//        // todo redirect with params (error)


        return "redirect:/admin/roles";
    }


}

