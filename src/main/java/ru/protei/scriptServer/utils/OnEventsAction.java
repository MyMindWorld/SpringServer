package ru.protei.scriptServer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.RoleRepository;
import ru.protei.scriptServer.service.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class OnEventsAction {

    Logger logger = LoggerFactory.getLogger(OnEventsAction.class);
    @Autowired
    ScriptsService scriptsService;
    @Autowired
    LogService logService;
    @Autowired
    VenvManager venvManager;
    @Autowired
    Utils utils;
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

    @EventListener(ApplicationReadyEvent.class)
    public void beforeStartup() {
        logger.info("AfterStartup invocation started!");
        utils.createDefaultFolders();
        scriptsService.getScriptsFromGit();
        scriptsService.updateAllScriptsConfigs();
        createAdminUserAndPrivileges();
        venvManager.createDefaultVenv();
    }

    // Creates two users with default roles for them
    public void createAdminUserAndPrivileges() {
        Privilege scripts_view
                = privilegeService.createPrivilegeIfNotFound("SCRIPTS_VIEW");
        Privilege admin_page_usage
                = privilegeService.createPrivilegeIfNotFound("ADMIN_PAGE_USAGE");
        Privilege scriptsUpdate
                = privilegeService.createPrivilegeIfNotFound("SCRIPTS_UPDATE");
        Privilege rolesAdmin
                = privilegeService.createPrivilegeIfNotFound("ROLES_SETTING");
        Privilege serverControl
                = privilegeService.createPrivilegeIfNotFound("SERVER_CONTROL");

        List<Privilege> adminPrivileges = Arrays.asList(
                scripts_view, admin_page_usage, scriptsUpdate, rolesAdmin, serverControl);
        Role adminRole = roleService.createRoleIfNotFound("ROLE_ADMIN", adminPrivileges, true);
        Role userRole = roleService.createRoleIfNotFound("ROLE_USER", Arrays.asList(scripts_view), true);
        Role roleAll = roleRepository.findByNameEquals("ROLE_ALL_SCRIPTS");

        User admin = new User();
        admin.setUsername("admin");
        admin.setLdapName("admin_admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setEmail("admin@admin.com");
        admin.setRoles(Arrays.asList(adminRole, userRole, roleAll));
        admin.setEnabled(true);

        userService.createUserIfNotFound(admin);

    }
}
