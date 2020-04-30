package ru.protei.scriptServer.service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.SneakyThrows;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.controller.ScriptWebSocketController;
import ru.protei.scriptServer.model.*;
import ru.protei.scriptServer.model.Enums.ServiceMessage;
import ru.protei.scriptServer.model.POJO.GitlabGroupsAnswer;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.repository.VenvRepository;
import ru.protei.scriptServer.utils.SystemIntegration.PythonScriptsRunner;
import ru.protei.scriptServer.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class ScriptsService {
    Logger logger = LoggerFactory.getLogger(ScriptsService.class);
    @Value("#{servletContext.contextPath}")
    private String servletContextPath;
    @Value("${scriptsGitUrl}")
    private String scriptsGitUrl;
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
    @Autowired
    VenvRepository venvRepository;
    @Autowired
    ScriptWebSocketController scriptWebSocketController;


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
                if ((jsonScript.name.equals(script.getName()))) {
                    script.setGroup_name(jsonScript.group);
                    script.setDisplay_name(jsonScript.display_name);
                    script.setVenv(jsonScript.venv);
                    script.setPython_version(jsonScript.python_version);
                    script.setRequirements(jsonScript.requirements);
                    script.setScript_path(jsonScript.script_path);
                    script.setParametersJson(jsonScript.paramsToJsonString());
                }

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

    public void runPythonScript(String[] params, Script script, String username, String uniqueSessionId) {
        if (script.getVenv() == null) {
            script.setVenv(utils.defaultVenvName);
        }
        Venv venvFromScript = venvRepository.findByNameEquals(script.getVenv());
        if (venvFromScript == null) {
            scriptWebSocketController.sendToSockFromServer(username, "Creating venv...", script.getName(), uniqueSessionId);
            venvManager.createIfNotExists(script.getVenv(), script.getRequirements());
            scriptWebSocketController.sendToSockFromServer(username, "Venv creation complete!", script.getName(), uniqueSessionId);
        } else {
            scriptWebSocketController.sendToSockFromServer(username, "Checking venv...", script.getName(), uniqueSessionId);
            venvManager.checkVenv(script, venvFromScript);
        }

        scriptWebSocketController.sendToSockFromServerService(username, "Starting script '" + script.getDisplay_name() + "'", script.getName(), uniqueSessionId, ServiceMessage.Started);
        pythonScriptsRunner.run(params, utils.getScriptsDirectory(), false, script, script.getVenv(), username, uniqueSessionId);
        logger.info("Exit code : " + pythonScriptsRunner.processExitcode);
        return;
    }

    @SneakyThrows
    public void getScriptsFromGit() {
        GitlabGroupsAnswer[] response =
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .get(scriptsGitUrl)
                        .then()
                        .extract()
                        .as(GitlabGroupsAnswer[].class);
        for (GitlabGroupsAnswer repository : response) {
            File repoFolder = utils.getFolderForScriptFromGit(repository.name);
            Git.cloneRepository()
                    .setURI(repository.httpUrlToRepo)
                    .setDirectory(repoFolder)
                    .setProgressMonitor(new SimpleProgressMonitor(logger))
                    .call()
                    .close();
            for (File fileInRepo : repoFolder.listFiles()) {
                if (fileInRepo.isDirectory() & fileInRepo.getName().equals("scripts")) {
                    for (File scriptsFile : fileInRepo.listFiles()) {
                        if (scriptsFile.getName().equals(".gitkeep")) {
                            continue;
                        }
                        try {
                            FileUtils.moveFileToDirectory(scriptsFile, utils.getScriptsDirectory(), true);
                        } catch (FileExistsException e) {
                            logger.error("scriptsFile '" + scriptsFile.getName() + "' already exists! Overwriting.");
                            FileUtils.forceDelete(new File(utils.getScriptsDirectory().toString() + "/" + scriptsFile.getName()));
                            FileUtils.moveFileToDirectory(scriptsFile, utils.getScriptsDirectory(), true);

                        }

                    }
                }
                if (fileInRepo.isDirectory() & fileInRepo.getName().equals("config")) {
                    for (File configFile : fileInRepo.listFiles()) {
                        if (configFile.getName().equals(".gitkeep")) {
                            continue;
                        }
                        try {
                            FileUtils.moveFileToDirectory(configFile, utils.getConfigDirectory(), true);
                        } catch (FileExistsException e) {
                            logger.error("configFile '" + configFile.getName() + "' already exists! Overwriting.");
                            FileUtils.forceDelete(new File(utils.getConfigDirectory().toString() + "/" + configFile.getName()));
                            FileUtils.moveFileToDirectory(configFile, utils.getConfigDirectory(), true);
                        }
                    }
                }
                if (fileInRepo.isDirectory() & fileInRepo.getName().equals("requirements")) {
                    for (File requirementFile : fileInRepo.listFiles()) {
                        if (requirementFile.getName().equals(".gitkeep")) {
                            continue;
                        }
                        try {
                            FileUtils.moveFileToDirectory(requirementFile, utils.getRequirementsDirectory(), true);
                        } catch (FileExistsException e) {
                            logger.error("requirementFile '" + requirementFile.getName() + "' already exists! Overwriting.");
                            FileUtils.forceDelete(new File(utils.getRequirementsDirectory().toString() + "/" + requirementFile.getName()));
                            FileUtils.moveFileToDirectory(requirementFile, utils.getRequirementsDirectory(), true);
                        }
                    }
                }

            }
        }


    }

    private static class SimpleProgressMonitor implements ProgressMonitor {
        public Logger logger;

        public SimpleProgressMonitor(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void start(int totalTasks) {
            logger.info("Starting work on " + totalTasks + " tasks");
        }

        @Override
        public void beginTask(String title, int totalWork) {
            logger.info("Start " + title + ": " + totalWork);
        }

        @Override
        public void update(int completed) {
//            logger.info(completed + "-");
        }

        @Override
        public void endTask() {
            logger.info("Done");
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }


}
