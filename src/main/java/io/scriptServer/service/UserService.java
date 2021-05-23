package io.scriptServer.service;

import io.scriptServer.model.Role;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.scriptServer.model.Privilege;
import io.scriptServer.model.Script;
import io.scriptServer.model.User;
import io.scriptServer.repository.RoleRepository;
import io.scriptServer.repository.ScriptRepository;
import io.scriptServer.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserDetails getUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.isEnabled(), true, true,
                true, getAuthorities(user));
    }

    public List<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream().flatMap(role -> roleService.getAuthorities(role).stream()).collect(Collectors.toList());
    }

    @Transactional
    public boolean checkPrivilege(User user, String privilege) {
        return user.getRoles()
                .stream().anyMatch(role -> role.getPrivileges()
                        .stream().anyMatch(privilegeFromRole -> privilegeFromRole.getName().equals(privilege)));
    }

    @Transactional
    public List<Script> getAllAvailableScriptsForUser(User user) {
        List<Script> scriptList = scriptRepository.findAll();
        List<Script> allowedScripts = new ArrayList<>();
        Collection<Privilege> allPrivilegesFromUser = getAllPrivilegesFromUser(user);
        for (Script script : scriptList) {
            if (allPrivilegesFromUser.stream()
                    .anyMatch(privilege -> privilege.getName().equals(script.getName()))) {
                allowedScripts.add(script);
            }
        }
        return allowedScripts;
    }

    public List<String> getAllAvailableScriptsForUserAsString(User user) {
        List<Script> allowedScripts = getAllAvailableScriptsForUser(user);
        List<String> allowedScriptsAsString = new ArrayList<>();
        for (Script script : allowedScripts) {
            allowedScriptsAsString.add(script.getName());
        }
        return allowedScriptsAsString;
    }

    @Transactional
    public Collection<Privilege> getAllPrivilegesFromUser(User user) {

        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());
        if (userFromRepo == null) {
            return null;
        }
        Collection<Privilege> privilegeCollection = new ArrayList<>();
        for (Role role : userFromRepo.getRoles()) {
            privilegeCollection.addAll(role.getPrivileges());
        }
        return privilegeCollection;
    }

    @Transactional
    public void createUserIfNotFound(User user) {
        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());
        if (userFromRepo == null) {
            userRepository.save(user);
        }
    }

    @Transactional
    public User createUser(String username, String password) {
        User userToCreate = new User();
        userToCreate.setUsername(username);
        userToCreate.setPassword(passwordEncoder.encode(password));
        userToCreate.setLdapName(username);
        userToCreate.setEmail(username + "@scriptServer.io"); // TODO UserCreation Form
        userToCreate.setEnabled(true);
        userToCreate.setRoles(Collections.singletonList(roleRepository.findByNameEquals("ROLE_USER")));

        User userFromRepo = userRepository.findByUsernameEquals(userToCreate.getUsername());
        if (userFromRepo == null) {
            return userRepository.save(userToCreate);
        } else {
            return null;
        }
    }

    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional
    public User getUserByEmail(String email) {
        return userRepository.findByEmailEquals(email);
    }

    @Transactional
    public User getUserByName(String username) {
        return userRepository.findByUsernameEquals(username);
    }


    public User findByUsernameEquals(String username) {
        return userRepository.findByUsernameEquals(username);
    }
}
