package ru.protei.scriptServer.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.PrivilegeRepository;
import ru.protei.scriptServer.repository.RoleRepository;
import ru.protei.scriptServer.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class TestLoginDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    // Находится в отдельном классе тк под вопросом. Нужно ли по умолчанию иметь пользователя или захардкодить на Ldap?

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;
        Privilege scripts_view
                = createPrivilegeIfNotFound("SCRIPTS_VIEW");
        Privilege admin_page_usage
                = createPrivilegeIfNotFound("ADMIN_PAGE_USAGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                scripts_view, admin_page_usage);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(scripts_view));

        Role adminRole = roleRepository.findByNameEquals("ROLE_ADMIN");
        Role userRole = roleRepository.findByNameEquals("ROLE_USER");

        User admin = new User();
        admin.setUsername("admin");
        admin.setLdapName("admin_admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setEmail("admin@admin.com");
        admin.setRoles(Arrays.asList(adminRole, userRole));
        admin.setEnabled(true);

        User user = new User();
        user.setUsername("user");
        user.setLdapName("user_user");
        user.setPassword(passwordEncoder.encode("user"));
        user.setEmail("user@user.com");
        user.setRoles(Arrays.asList(userRole));
        user.setEnabled(true);


        createUserIfNotFound(admin);
        createUserIfNotFound(user);

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByNameEquals(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(
            String name, Collection<Privilege> privileges) {

        Role role = roleRepository.findByNameEquals(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }

    @Transactional
    void createUserIfNotFound(
            User user) {

        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());
        if (userFromRepo == null) {
            userRepository.save(user);
        }
    }
}
