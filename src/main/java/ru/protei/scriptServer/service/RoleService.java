package ru.protei.scriptServer.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.RoleRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class RoleService {
    private RoleRepository roleRepository;
    private PrivilegeService privilegeService;

    public List<? extends GrantedAuthority> getAuthorities(Role role) {
        return role.getPrivileges().stream().map(Privilege::getName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public List<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return roles.stream().map(this::getAuthorities).flatMap(Collection::stream).collect(Collectors.toList());
    }


    @Transactional
    public Role createRoleIfNotFound(
            String name, List<Privilege> privileges) {

        if (roleRepository.findByNameEquals(name) == null) {
            Role role = Role.builder()
                    .name(name)
                    .privileges(privileges)
                    .build();
            roleRepository.save(role);
            log.info("New role '" + name + "' created");
            return role;
        }
        throw new IllegalStateException("Exists!");
//        return null;
    }

    @Transactional
    public Role createProtectedRoleIfNotFound(
            String name, List<Privilege> privileges) {
        if (roleRepository.findByNameEquals(name) == null) {
            Role role = Role.builder()
                    .name(name)
                    .is_protected(true)
                    .privileges(privileges)
                    .build();
            roleRepository.save(role);
            log.info("New role '" + name + "' created");
            return role;
        }
        return null; // null нужен чтобы пометить что роль уже существовала
    }

    @Transactional
    public Role updateRolePrivileges(
            String name, List<Privilege> privileges) {

        Role role = roleRepository.findByNameEquals(name);
        if (role == null) {
            log.warn("Role '" + name + "' not found!");
            return null;
        }
        role.setPrivileges(privileges);
        roleRepository.save(role);
        log.info("Role '" + name + "' updated");
        return role;
    }

    @Transactional
    public Role updateRoleName(
            String name, String newName) {

        Role role = roleRepository.findByNameEquals(name);
        if (role == null) {
            log.warn("Role '" + name + "' not found!");
            return null;
        }
        role.setName(newName);
        roleRepository.save(role);
        log.info("Role '" + name + "' updated, new name : " + newName);
        return role;
    }

    @Transactional
    public void deleteRoleFromUsers(Role roleToDelete) {
        Collection<User> usersWithRole = roleToDelete.getUsers();
        for (User userWithRole : usersWithRole) {
            userWithRole.setRoles(userWithRole.getRoles().stream().filter(userRole -> userRole != roleToDelete).collect(Collectors.toList()));
        }
        roleToDelete.setUsers(null);
        roleRepository.delete(roleToDelete);
    }

    @Transactional
    public Optional<Role> findRoleByPrivileges(List<Privilege> privileges) {
        List<Role> resultList = roleRepository.findAll();

        for (Role contestant : resultList) {
            if (contestant.getPrivileges().size() == privileges.size() & contestant.getPrivileges().containsAll(privileges)) {
                log.info("Role with same privileges found! '" + contestant.getName() + "' " + contestant.getPrivileges());
                return Optional.of(contestant);
            }

        }
        return Optional.empty();
    }


    public void updateRoleAllPrivileges() {
        List<Privilege> allPrivileges = privilegeService.returnAllPrivileges();

        Role role_all = createProtectedRoleIfNotFound("ALL_PRIVILEGES_ROLE", allPrivileges);
        if (role_all == null)
            updateRolePrivileges("ALL_PRIVILEGES_ROLE", allPrivileges);
    }

    public Role findByNameEquals(String name) {
        return roleRepository.findByNameEquals(name);
    }
}
