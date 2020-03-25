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
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.PrivilegeService;
import ru.protei.scriptServer.service.RoleService;
import ru.protei.scriptServer.service.UserService;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class TestLoginDataLoader {
    Logger logger = LoggerFactory.getLogger(TestLoginDataLoader.class);

    // Находится в отдельном классе тк под вопросом. Нужно ли по умолчанию иметь пользователя или захардкодить на Ldap?

    boolean alreadySetup = false;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private LogService logService;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void fillTestData() {

        userService.deleteAll();

        if (alreadySetup)
            return;
        Privilege scripts_view
                = privilegeService.createPrivilegeIfNotFound("SCRIPTS_VIEW");
        Privilege admin_page_usage
                = privilegeService.createPrivilegeIfNotFound("ADMIN_PAGE_USAGE");
        Privilege scriptsUpdate
                = privilegeService.createPrivilegeIfNotFound("SCRIPTS_UPDATE");
        Privilege rolesAdmin
                = privilegeService.createPrivilegeIfNotFound("ROLES_SETTING");

        List<Privilege> adminPrivileges = Arrays.asList(
                scripts_view, admin_page_usage,scriptsUpdate,rolesAdmin);
        Role adminRole = roleService.createRoleIfNotFound("ROLE_ADMIN", adminPrivileges,true);
        Role userRole = roleService.createRoleIfNotFound("ROLE_USER", Arrays.asList(scripts_view),true);
        Role roleAll = roleRepository.findByNameEquals("ROLE_ALL_SCRIPTS");

        User admin = new User();
        admin.setUsername("admin");
        admin.setLdapName("admin_admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setEmail("admin@admin.com");
        admin.setRoles(Arrays.asList(adminRole, userRole,roleAll));
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

        Integer logRepSizeForTest = 500;
        Integer privilegeRepSizeForTest = 70;

        if (logService.logSize() < logRepSizeForTest) {
            for (int count = 0; count < logRepSizeForTest; count++) {
                int leftLimit = 97; // letter 'a'
                int rightLimit = 122; // letter 'z'
                int paramsLength = 500;
                int errorLength = 500;
                Random random = new Random();
                StringBuilder bufferError = new StringBuilder(errorLength);
                StringBuilder bufferParams = new StringBuilder(errorLength);
                for (int i = 0; i < errorLength; i++) {
                    int randomLimitedInt = leftLimit + (int)
                            (random.nextFloat() * (rightLimit - leftLimit + 1));
                    bufferError.append((char) randomLimitedInt);
                }
                for (int i = 0; i < paramsLength; i++) {
                    int randomLimitedInt = leftLimit + (int)
                            (random.nextFloat() * (rightLimit - leftLimit + 1));
                    bufferParams.append((char) randomLimitedInt);
                }
                String genError = bufferError.toString();
                String genParams = bufferParams.toString();

                logService.logAction("Test", "127.5.5.5", "TestNumber " + count, genParams, genError);
            }

        }

        if (privilegeService.returnAllPrivileges().size() < privilegeRepSizeForTest) {
            for (int count = 0; count < privilegeRepSizeForTest; count++) {
                int leftLimit = 97; // letter 'a'
                int rightLimit = 122; // letter 'z'
                int privilegeNameLength = 20;
                Random random = new Random();
                StringBuilder privilegeName = new StringBuilder(privilegeNameLength);
                for (int i = 0; i < privilegeNameLength; i++) {
                    int randomLimitedInt = leftLimit + (int)
                            (random.nextFloat() * (rightLimit - leftLimit + 1));
                    privilegeName.append((char) randomLimitedInt);
                }

                String genName = privilegeName.toString();

                privilegeService.createPrivilegeIfNotFound(genName);
            }

        }


        alreadySetup = true;
    }


}
