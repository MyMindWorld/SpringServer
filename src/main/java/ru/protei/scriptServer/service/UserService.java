package ru.protei.scriptServer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.*;
import ru.protei.scriptServer.repository.PasswordTokenRepository;
import ru.protei.scriptServer.repository.RoleRepository;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

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
    public void createUserIfNotFound(
            User user) {

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

    @Transactional
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordTokenRepository.findByTokenEquals(token).getUser());
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByTokenEquals(token);
        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    @Transactional
    public void removePasswordResetToken(String token) {
        passwordTokenRepository.deleteByTokenEquals(token);
    }

    @Transactional
    public void removeAllUserPasswordResetTokens(User user) {
        passwordTokenRepository.deleteByUserEquals(user);
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    public User findByUsernameEquals(String username) {
        return userRepository.findByUsernameEquals(username);
    }
}
