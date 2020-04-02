package ru.protei.scriptServer.service;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.JsonScript;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.utils.SystemIntegration.PythonScriptsRunner;
import ru.protei.scriptServer.utils.Utils;

import java.io.IOException;
import java.util.List;

@Service
public class ScriptsHandler {
    Logger logger = LoggerFactory.getLogger(ScriptsHandler.class);
    @Value("#{servletContext.contextPath}")
    private String servletContextPath;
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private RoleService roleService;
    @Autowired
    LogService logService;
    @Autowired
    Utils utils;
    @Autowired
    PythonScriptsRunner pythonScriptsRunner;
    @Autowired
    VenvManager venvManager;


    @SneakyThrows
    public void updateAllScriptsConfigs() {
        logger.info("Database cleanup started!");

        if (scriptRepository.findAll().size() > 0) {
            scriptRepository.deleteAll();
            logger.info("Database is now clean");
        }

        Resource[] configs = utils.getConfigs();
        if (configs == null) {
            logger.warn("Scripts folder is empty!");
            return;
        }

        for (Resource config : configs) {

            Script script = new Script();
            try {

                JsonScript jsonScript = utils.parseJsonToObject(config.getInputStream());
                // todo if parsing straight to db really hard?
                script.setName(jsonScript.name);
                script.setGroup_name(jsonScript.group);
                script.setDisplay_name(jsonScript.display_name);
                script.setVenv(jsonScript.venv);
                script.setPython_version(jsonScript.python_version);
                script.setRequirements(jsonScript.requirements);
                script.setScript_path(jsonScript.script_path);
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
        updateRoleAllScripts();
    }

    @SneakyThrows
    public Script updateSpecifiedScriptConfig(Script script) {
        logger.info("Database cleanup started!");
        scriptRepository.delete(script);

        Resource[] configs = utils.getConfigs();
        if (configs == null) {
            logger.warn("Scripts folder is empty!");
            return null;
        }
        for (Resource config : configs) {
            try {

                JsonScript jsonScript = utils.parseJsonToObject(config.getInputStream());
                if (!(jsonScript.name.equals(script.getName()))) {
                    logger.info(jsonScript.name + " !+ " + script.getName());
                    continue;
                }
                script.setGroup_name(jsonScript.group);
                script.setDisplay_name(jsonScript.display_name);
                script.setVenv(jsonScript.venv);
                script.setPython_version(jsonScript.python_version);
                script.setRequirements(jsonScript.requirements);
                script.setScript_path(jsonScript.script_path);
                script.setParametersJson(jsonScript.paramsToJsonString());
            } catch (IOException e) {
                logger.error("Mapping json to object failed!", e);
            }
            scriptRepository.save(script);
        }
        if (scriptRepository.findByNameEquals(script.getName()) != null) {
            logger.info("Script with name '" + script.getName() + "' successfully updated");
            return script;
        } else {
            logger.error("Script config was not found during update!");
            return null;
        }
    }


    public Script updateSpecifiedScriptConfigAndDropVenv(Script script) {
        venvManager.deleteVenv(script.getVenv());
        return updateSpecifiedScriptConfig(script);
    }

    public Script updateSpecifiedScriptConfigAndRecreateVenv(Script script) {
        venvManager.deleteVenv(script.getVenv());
        Script updatedScript = updateSpecifiedScriptConfig(script);
        venvManager.createVenv(script.getVenv(), script.getRequirements());
        return updatedScript;
    }


    public void updateRoleAllScripts() {
        List<Privilege> allPrivileges = privilegeService.returnAllPrivileges();

        Role role_all = roleService.createRoleIfNotFound("ROLE_ALL_SCRIPTS", allPrivileges, true);
        if (role_all == null)
            roleService.updateRole("ROLE_ALL_SCRIPTS", allPrivileges, true);
    }

    @SneakyThrows
    public void runPythonScript(String[] params, Script script,String username) {
        if (script.getVenv() == null) {
            script.setVenv(utils.defaultVenvName);
        } else {
            venvManager.createIfNotExists(script.getVenv(), script.getRequirements());
        }

        pythonScriptsRunner.run(params, utils.getScriptsDirectory(), false, script, script.getVenv(),username);
        logger.info("Exit code : " + pythonScriptsRunner.processExitcode);
    }


}
