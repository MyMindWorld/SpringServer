package ru.protei.scriptServer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.RoleRepository;
import ru.protei.scriptServer.repository.UserRepository;

import javax.transaction.Transactional;

@Service
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        userToCreate.setEmail(username + "@protei.ru");
        userToCreate.setEnabled(true);

        User userFromRepo = userRepository.findByUsernameEquals(userToCreate.getUsername());
        if (userFromRepo == null) {
            return userRepository.save(userToCreate);
        } else {
            return null;
        }
    }

    @Transactional
    public void deleteAll() {
        logger.warn("Removing all users!!!");

        userRepository.deleteAll();
    }
}
