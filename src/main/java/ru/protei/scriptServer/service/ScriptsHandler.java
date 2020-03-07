package ru.protei.scriptServer.service;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.JsonScript;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.utils.Utils;

import java.io.IOException;
import java.util.List;

@Service
public class ScriptsHandler {
    Logger logger = LoggerFactory.getLogger(ScriptsHandler.class);
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private RoleService roleService;
    @Autowired
    Utils utils;


    @SneakyThrows
    public void updateScriptsInDb() {
        logger.info("Database cleanup started!");

        if (scriptRepository.findAll().size() > 0) {
            scriptRepository.deleteAll();
            logger.info("Database is now clean");
        }

        Resource[] ScriptsPath = utils.getScriptsPath();
        if (ScriptsPath == null) {
            logger.warn("Scripts folder is empty!");
            return;
        }

        for (Resource resource : ScriptsPath) {

            Script script = new Script();
            try {

                JsonScript jsonScript = utils.parseJsonToObject(resource.getInputStream());
                // todo check if script (.py) exists
                script.setName(jsonScript.name);
                script.setDisplay_name(jsonScript.display_name);
                script.setParametersJson(jsonScript.paramsToJsonString());
            } catch (IOException e) {
                logger.error("Mapping json to object failed!", e);
            }
            if (scriptRepository.findByNameEquals(script.getName()) != null) {
                logger.error("Script with name '" + script.getName() + "' already exists! It won't be saved.");
                continue;
            }
            scriptRepository.save(script);
            privilegeService.createPrivilegeIfNotFound(script.getName());


        }


        List<Privilege> allPrivilege = privilegeService.returnAllPrivileges();

        Role role_all = roleService.createRoleIfNotFound("ROLE_ALL", allPrivilege);
        if (role_all == null)
            roleService.updateRole("ROLE_ALL", allPrivilege);

        logger.warn(scriptRepository.findAll().toString());


    }


}
