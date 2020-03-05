package ru.protei.scriptServer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.RoleRepository;
import ru.protei.scriptServer.service.PrivilegeService;
import ru.protei.scriptServer.service.RoleService;
import ru.protei.scriptServer.service.UserService;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Component
public class TestLoginDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {
    Logger logger = LoggerFactory.getLogger(TestLoginDataLoader.class);

    // Находится в отдельном классе тк под вопросом. Нужно ли по умолчанию иметь пользователя или захардкодить на Ldap?

    boolean alreadySetup = false;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        userService.deleteAll();

        if (alreadySetup)
            return;
        Privilege scripts_view
                = privilegeService.createPrivilegeIfNotFound("SCRIPTS_VIEW");
        Privilege admin_page_usage
                = privilegeService.createPrivilegeIfNotFound("ADMIN_PAGE_USAGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                scripts_view, admin_page_usage);
        roleService.createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        roleService.createRoleIfNotFound("ROLE_USER", Arrays.asList(scripts_view));

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


        userService.createUserIfNotFound(admin);
        userService.createUserIfNotFound(user);

        alreadySetup = true;
    }


}
