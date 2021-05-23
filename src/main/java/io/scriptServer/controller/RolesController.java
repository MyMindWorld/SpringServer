package io.scriptServer.controller;

import io.scriptServer.model.Role;
import io.scriptServer.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import io.scriptServer.model.Privilege;
import io.scriptServer.repository.LogRepository;
import io.scriptServer.repository.PrivilegeRepository;
import io.scriptServer.repository.RoleRepository;
import io.scriptServer.service.RoleService;
import io.scriptServer.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static io.scriptServer.utils.Utils.getUsername;

@Controller
public class RolesController {
    Logger logger = LoggerFactory.getLogger(RolesController.class);

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

    @GetMapping(value = "/admin/roles")
    public String roles(Model model) {

        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("privileges", privilegeRepository.findAll());


        return "roles";
    }

    @PostMapping(value = "/admin/create_role")
    public String createRole(@ModelAttribute("name") String roleName, @RequestParam("privileges") List<Long> privileges_raw, Model model, HttpServletRequest request) {
        roleName = roleName.trim();

        Iterable<Long> iterable = privileges_raw;

        List<Privilege> privileges = privilegeRepository.findAllById(iterable);

        logger.info("Received create role request from : '" + getUsername() + "' adding " + roleName);
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Role add", "Role name : '" + roleName + "', Privileges : " + privileges);

        Optional<Role> sameRole = roleService.findRoleByPrivileges(privileges);
        if (sameRole.isPresent()) {
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Attempt of creating already existing role ", "Role name : '" + roleName + "', Privileges : " + privileges);
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Role with this privileges already exists, it's called '" + sameRole.get().getName() + "' Try using it for your purposes.");
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

    @PostMapping(value = "/admin/update_role")
    public String updateRole(@ModelAttribute("name") String roleName, @ModelAttribute("nameNew") String newRoleName, @RequestParam("privilegesUpdate") List<Long> privileges_raw, Model model, HttpServletRequest request) {
        roleName = roleName.trim();
        Iterable<Long> iterable = privileges_raw;
        List<Privilege> privileges = privilegeRepository.findAllById(iterable);
        boolean isRoleNameUpdated = newRoleName != roleName & !newRoleName.isEmpty();

        logger.info("Received update role request from : '" + getUsername() + "' updating role '" + roleName + "'");
        logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Role update", "Role name : '" + roleName + "', new privileges : " + privileges);

        Role roleFromRepo = roleRepository.findByNameEquals(roleName);
        if (roleFromRepo.is_protected()) {
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Attempt of updating protected role ", "Role name : '" + roleName + "', Privileges : " + privileges);
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Updating protected roles is forbidden!");
        } else {
            Role updatedRole;
            if (isRoleNameUpdated) {
                logger.info("New role name : '" + newRoleName + "'");
                updatedRole = roleService.updateRoleName(roleName, newRoleName);
            } else {
                updatedRole = roleService.updateRolePrivileges(roleName, privileges);
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

    @PostMapping(value = "/admin/delete_role")
    public String deleteRole(Role role, Model model, HttpServletRequest request) {

        logger.info("Received role delete from : '" + getUsername() + "' deleting role '" + role.getName() + "'");
        Role roleFromRepo = roleRepository.findByNameEquals(role.getName());


        if (roleFromRepo != null) {
            if (roleFromRepo.is_protected()) {
                logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Attempt of deleting protected role ", "Role name : '" + role.getName() + "', Privileges : " + role.getPrivileges());
                model.addAttribute("error", true);
                model.addAttribute("errorMessage", "Deleting protected roles is forbidden!");
                return roles(model);
            }
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Delete role", roleFromRepo.toString());
            if (roleFromRepo.getUsers().size() != 0) {
                roleService.deleteRoleFromUsers(roleFromRepo);
            }
            roleRepository.delete(roleFromRepo);
            model.addAttribute("success", true);
            model.addAttribute("successMessage", "Deleted role '" + roleFromRepo.getName() + "' successfully!");
        } else {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Role with name '" + role.getName() + "' not found!!! Please contact admin");
            logService.logAction(request.getRemoteUser(), request.getRemoteAddr(), "Role Delete", role.getName(), "ROLE NOT FOUND!");

        }
        return roles(model);
    }

}
