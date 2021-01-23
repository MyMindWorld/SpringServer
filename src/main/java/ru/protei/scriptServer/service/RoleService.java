package ru.protei.scriptServer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.RoleRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Service
public class RoleService {
    Logger logger = LoggerFactory.getLogger(RoleService.class);


    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeService privilegeService;


    @Transactional
    public Role createRoleIfNotFound(
            String name, List<Privilege> privileges) {

        if (roleRepository.findByNameEquals(name) == null) {
            Role role = Role.builder()
                    .name(name)
                    .privileges(privileges)
                    .build();
            roleRepository.save(role);
            logger.info("New role '" + name + "' created");
            return role;
        }
        return null;
    }

    @Transactional
    public Role createRoleIfNotFound(
            String name, List<Privilege> privileges, boolean is_protected) {
        if (roleRepository.findByNameEquals(name) == null) {
            Role role = Role.builder()
                    .name(name)
                    .is_protected(is_protected)
                    .privileges(privileges)
                    .build();
            roleRepository.save(role);
            logger.info("New role '" + name + "' created");
            return role;
        }
        return null; // null нужен чтобы пометить что роль уже существовала
    }

    @Transactional
    public Role updateRole(
            String name, List<Privilege> privileges) {

        Role role = roleRepository.findByNameEquals(name);
        if (role == null) {
            logger.warn("Role '" + name + "' not found!");
            return null;
        }
        role.setPrivileges(privileges);
        roleRepository.save(role);
        logger.info("Role '" + name + "' updated");
        return role;
    }

    @Transactional
    public Role updateRole(
            String name, String newName, List<Privilege> privileges) {

        Role role = roleRepository.findByNameEquals(name);
        if (role == null) {
            logger.warn("Role '" + name + "' not found!");
            return null;
        }
        role.setPrivileges(privileges);
        role.setName(newName);
        roleRepository.save(role);
        logger.info("Role '" + name + "' updated, new name : " + newName);
        return role;
    }

    @Transactional
    public Role updateRole(
            String name, List<Privilege> privileges, boolean is_protected) {
        Role role = roleRepository.findByNameEquals(name);
        role.setPrivileges(privileges);
        role.setName(name);
        role.set_protected(is_protected);
        roleRepository.save(role);
        logger.info("Role '" + name + "' updated");
        return role;
    }

    @Transactional
    public void deleteRoleFromUsers(Role role) {
        Collection<User> usersWithRole = role.getUsers();
        for (User userWithRole : usersWithRole) {
            Collection<Role> userRoles = userWithRole.getRoles();
            userRoles.remove(role);
        }
    }

    @Transactional
    public Role findRoleByPrivileges(List<Privilege> privileges) {
        List<Role> resultList = roleRepository.findAll();

        for (Role contestant : resultList) {
            if (contestant.getPrivileges().size() == privileges.size() & contestant.getPrivileges().containsAll(privileges)) {
                logger.info("Role with same privileges  found! '" + contestant.getName() + "'  " + contestant.getPrivileges());
                return contestant;
            }

        }
        return null;
    }


    public void updateRoleAllPrivileges() {
        List<Privilege> allPrivileges = privilegeService.returnAllPrivileges();

        Role role_all = createRoleIfNotFound("ALL_PRIVILEGES_ROLE", allPrivileges, true);
        if (role_all == null)
            updateRole("ALL_PRIVILEGES_ROLE", allPrivileges, true);
    }
}
