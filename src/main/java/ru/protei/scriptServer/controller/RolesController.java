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
import ru.protei.scriptServer.repository.*;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.RoleService;
import ru.protei.scriptServer.service.ScriptsHandler;
import ru.protei.scriptServer.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.scriptServer.utils.Utils.getUsername;

@Controller
public class RolesController {
    Logger logger = LoggerFactory.getLogger(RolesController.class);
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
    LogService logService;

    @RequestMapping(value = "/admin/roles", method = RequestMethod.GET)
    public String roles(Model model) {

        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("privileges", privilegeRepository.findAll());


        return "roles";
    }

    @RequestMapping(value = "/admin/create_role", method = RequestMethod.POST)
    public String createRole(@ModelAttribute("name") String roleName, @RequestParam("privileges") List<Long> privileges_raw, Model model, HttpServletRequest request) {
        roleName = roleName.trim();


        Iterable<Long> iterable = privileges_raw;

        List<Privilege> privileges = privilegeRepository.findAllById(iterable);


        logger.info("Received create role request from : '" + getUsername() + "' adding " + roleName);
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Role add", "Role name : '" + roleName + "', Privileges : " + privileges);

        Role sameRole = roleService.findRoleByPrivileges(privileges);
        if (sameRole != null) {
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Attempt of creating already existing role ", "Role name : '" + roleName + "', Privileges : " + privileges);
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Role with this privileges already exists, it's called '" + sameRole.getName() + "' Try using it for your purposes.");
        } else {
            Role createdRole = roleService.createRoleIfNotFound(roleName, privileges);
            if (createdRole != null) {
                logger.info("Role created");
                model.addAttribute("success", true);
                model.addAttribute("successMessage", "Role '" + roleName + "' is successfully created!");
            } else {
                logger.warn("Role with this name exists!");
                model.addAttribute("error", true);
                model.addAttribute("errorMessage", "Role '" + roleName + "' is already exists! Updating roles should be done through table below");
            }

        }


        return roles(model);
    }

    @RequestMapping(value = "/admin/update_role", method = RequestMethod.POST)
    public String updateRole(@ModelAttribute("name") String roleName, @ModelAttribute("nameNew") String newRoleName, @RequestParam("privilegesUpdate") List<Long> privileges_raw, Model model, HttpServletRequest request) {
        roleName = roleName.trim();
        Iterable<Long> iterable = privileges_raw;
        List<Privilege> privileges = privilegeRepository.findAllById(iterable);
        boolean isRoleNameUpdated = newRoleName != roleName & !newRoleName.isEmpty();

        logger.info("Received update role request from : '" + getUsername() + "' adding " + roleName);
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Role update", "Role name : '" + roleName + "', new privileges : " + privileges);

        Role sameRole = roleService.findRoleByPrivileges(privileges);
        if (sameRole != null & !isRoleNameUpdated) {
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Attempt of creating already existing role ", "Role name : '" + roleName + "', Privileges : " + privileges);
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Role with this privileges already exists, it's called '" + sameRole.getName() + "' Try using it for your purposes.");
        } else {
            Role updatedRole;
            if (isRoleNameUpdated) {
                logger.info("New role name : '" + newRoleName + "'");
                updatedRole = roleService.updateRole(roleName, newRoleName, privileges);
            } else {
                updatedRole = roleService.updateRole(roleName, privileges);
            }
            if (updatedRole != null) {
                logger.info("Role updated!");
                model.addAttribute("success", true);
                model.addAttribute("successMessage", "Role '" + roleName + "' is successfully updated!");
            } else {
                logger.warn("Horrible mistake happened!");
                model.addAttribute("error", true);
                model.addAttribute("errorMessage", "Role '" + roleName + "' updating horribly failed!");
            }

        }


        return roles(model);
    }

    @RequestMapping(value = "/admin/delete_role", method = RequestMethod.POST)
    public String deleteRole(Role role, Model model, HttpServletRequest request) {

        logger.info("Received role delete from : '" + getUsername() + "' deleting role '" + role.getName() + "'");
        Role roleFromRepo = roleRepository.findByNameEquals(role.getName());


        if (roleFromRepo != null) {
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Delete role", roleFromRepo.toString());
            if (roleFromRepo.getUsers().size() != 0) {
                roleService.deleteRoleFromUsers(roleFromRepo);
            }
            roleRepository.delete(roleFromRepo);
            model.addAttribute("success", true);
            model.addAttribute("successMessage", "Deleted role '" + roleFromRepo.getName() + "' successfully!");
        } else {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "User with username '" + roleFromRepo.getName() + "' not found!!! Please contact admin");
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Role Delete", roleFromRepo.getName(), "USER NOT FOUND!");

        }
        return roles(model);
    }

}
