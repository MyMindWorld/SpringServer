package ru.protei.scriptServer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.PrivilegeRepository;
import ru.protei.scriptServer.repository.RoleRepository;
import ru.protei.scriptServer.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Service
public class RoleService {
    Logger logger = LoggerFactory.getLogger(RoleService.class);


    @Autowired
    private RoleRepository roleRepository;


    @Transactional
    public Role createRoleIfNotFound(
            String name, List<Privilege> privileges) {


        Role role = roleRepository.findByNameEquals(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
            logger.info("New role '" + name + "' created");
            return role;
        }
        return null;
    }

    @Transactional
    public Role createRoleIfNotFound(
            String name, List<Privilege> privileges, boolean is_protected) {
        Role role = roleRepository.findByNameEquals(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            role.set_protected(is_protected);
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


}
