package io.scriptServer.utils;

import io.scriptServer.model.Role;
import io.scriptServer.model.User;
import io.scriptServer.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.scriptServer.model.Privilege;
import io.scriptServer.repository.RoleRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class OnEventsAction {

    Logger logger = LoggerFactory.getLogger(OnEventsAction.class);
    @Autowired
    ScriptsService scriptsService;
    @Autowired
    LogService logService;
    @Autowired
    VenvService venvService;
    @Autowired
    Utils utils;
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("#{new Boolean('${updateScriptsOnStartup:false}')}")
    private Boolean updateScriptsOnStartup;

    @EventListener(ApplicationReadyEvent.class)
    public void beforeStartup() {
        logger.info("AfterStartup invocation started!");
        utils.createDefaultFolders();
        if (updateScriptsOnStartup) {
            scriptsService.getScriptsFromGit();
        }
        scriptsService.updateAllScriptsConfigs();
        createAdminUserAndPrivileges();
        venvService.createDefaultVenv();
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
        Privilege filesUpload
                = privilegeService.createPrivilegeIfNotFound("FILES_UPLOAD");
        Privilege filesEdit
                = privilegeService.createPrivilegeIfNotFound("FILES_EDIT");

        List<Privilege> adminPrivileges = Arrays.asList(
                scripts_view, admin_page_usage, scriptsUpdate, rolesAdmin, serverControl, filesUpload, filesEdit);
        Role adminRole = roleService.createProtectedRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        Role userRole = roleService.createProtectedRoleIfNotFound("ROLE_USER", Collections.singletonList(scripts_view));
        Role roleAll = roleRepository.findByNameEquals("ALL_PRIVILEGES_ROLE");

        User admin = new User();
        admin.setUsername("admin");
        admin.setLdapName("admin_admin");
        admin.setPassword(passwordEncoder.encode("admin_admin"));
        admin.setEmail("admin@admin.com");
        admin.setRoles(Arrays.asList(adminRole, userRole, roleAll));
        admin.setEnabled(true);

        userService.createUserIfNotFound(admin);
        roleService.updateRoleAllPrivileges();

    }
}
