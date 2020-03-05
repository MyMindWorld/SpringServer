package ru.protei.scriptServer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.repository.PrivilegeRepository;
import ru.protei.scriptServer.repository.RoleRepository;
import ru.protei.scriptServer.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RoleService {
    Logger logger = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;


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


}
