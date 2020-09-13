package ru.protei.scriptServer.service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.SneakyThrows;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.config.ProcessQueueConfig;
import ru.protei.scriptServer.controller.ScriptWebSocketController;
import ru.protei.scriptServer.model.Enums.ServiceMessage;
import ru.protei.scriptServer.model.POJO.GitlabGroupsAnswer;
import ru.protei.scriptServer.model.POJO.JsonScript;
import ru.protei.scriptServer.model.POJO.RunningScript;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.model.Venv;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.repository.VenvRepository;
import ru.protei.scriptServer.utils.SystemIntegration.PythonScriptsRunner;
import ru.protei.scriptServer.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

@Service
public class ScriptsService {
    Logger logger = LoggerFactory.getLogger(ScriptsService.class);
    @Value("#{servletContext.contextPath}")
    private String servletContextPath;
    @Value("${scriptsGitUrl:NONE}") // todo default param
    private String scriptsGitUrl;
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    public RoleService roleService;
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
    @Autowired
    ProcessQueueConfig processQueueConfig;


    @SneakyThrows
    public void updateAllScriptsConfigs() {
        logger.info("Database cleanup started!");

        if (scriptRepository.findAll().size() > 0) {
            scriptRepository.deleteAll();
            logger.info("Database is now clean");
        }

        File[] configs = utils.getConfigs();
        if (configs == null) {
            logger.warn("Scripts folder is empty!");
            return;
        }

        for (File config : configs) {
            if (config.isDirectory()) {
                continue;
            }
            logger.info("Parsing '" + config.getName() + "','" + config.getAbsolutePath());
            Script script = new Script();
            try {

                JsonScript jsonScript = utils.parseJsonToObject(FileUtils.openInputStream(config));
                // todo if parsing straight to db really hard?
                script.setName(jsonScript.name);
                script.setGroup_name(jsonScript.group);
                script.setDisplayName(jsonScript.display_name);
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
        roleService.updateRoleAllPrivileges();
    }

    @SneakyThrows
    public Script updateSpecifiedScriptConfig(Script script) {
        logger.info("Database cleanup started!");
        scriptRepository.delete(script);

        File[] configs = utils.getConfigs();
        if (configs == null) {
            logger.warn("Scripts folder is empty!");
            return null;
        }
        for (File config : configs) {
            if (config.isDirectory()) {
                continue;
            }

            try {
                JsonScript jsonScript = utils.parseJsonToObject(FileUtils.openInputStream(config));
                if ((jsonScript.name.equals(script.getName()))) {
                    script.setGroup_name(jsonScript.group);
                    script.setDisplayName(jsonScript.display_name);
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


    @SneakyThrows
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

        scriptWebSocketController.sendToSockFromServerService(username, "Starting script '" + script.getDisplayName() + "'", script.getName(), uniqueSessionId, ServiceMessage.Started);
        pythonScriptsRunner.run(params, utils.getScriptsDirectory(), false, script, script.getVenv(), username, uniqueSessionId);
        logger.info("Exit code : " + pythonScriptsRunner.processExitcode);

        if (Thread.currentThread().isInterrupted()) {
            logger.info("Thread is interrupted, removing from queue is not necessary");
        } else {
            logger.info("Removing script from queue");

            RunningScript runningScript = new RunningScript();
            runningScript.setScriptName(script.getName());
            runningScript.setSessionId(uniqueSessionId);
            runningScript.setUserName(username);
            runningScript.setThreadName(Thread.currentThread().getName());
            HashMap<RunningScript, Process> processMap;
            while (!((processMap = processQueueConfig.processBlockingQueue().take()).keySet().contains(runningScript))) {
                processQueueConfig.processBlockingQueue().put(processMap);
            }
        }

        logger.info("Run script thread is stopped.");

        return;
    }

    public void getScriptsFromGit() {
        if (scriptsGitUrl.contains("NONE")) {
            logger.info("Skipping scripts update");
            return;
        }
        try {
            InetAddress.getByName("www.git.protei.ru");
            GitlabGroupsAnswer[] response =
                    RestAssured.given()
                            .contentType(ContentType.JSON)
                            .get(scriptsGitUrl)
                            .then()
                            .extract()
                            .as(GitlabGroupsAnswer[].class);
            for (GitlabGroupsAnswer repository : response) {
                File repoFolder = utils.getFolderForScriptsFromGit(repository.name);
                Git.cloneRepository()
                        .setURI(repository.httpUrlToRepo)
                        .setDirectory(repoFolder)
                        .setProgressMonitor(new SimpleProgressMonitor(logger))
                        .call()
                        .close();

                for (File fileInRepo : repoFolder.listFiles()) {
                    if (fileInRepo.isDirectory() & fileInRepo.getName().equals("scripts")) {
                        copyAllFilesFromDirectory(fileInRepo, utils.getScriptsDirectory());
                    }
                    if (fileInRepo.isDirectory() & fileInRepo.getName().equals("config")) {
                        copyAllFilesFromDirectory(fileInRepo, utils.getConfigDirectory());
                    }
                    if (fileInRepo.isDirectory() & fileInRepo.getName().equals("requirements")) {
                        copyAllFilesFromDirectory(fileInRepo, utils.getRequirementsDirectory());
                    }
                }
            }
        } catch (UnknownHostException | GitAPIException e) {
            logger.error("Git is unavailable! Can't update scripts from git");
            return;
        }
    }

    @SneakyThrows
    public void copyAllFilesFromDirectory(File sourceFileDir, File destFile) {
        for (File fileFromDir : sourceFileDir.listFiles()) {
            if (fileFromDir.getName().equals(".gitkeep")) {
                continue;
            }
            try {
                if (fileFromDir.isDirectory()) {
                    FileUtils.moveDirectoryToDirectory(fileFromDir, destFile, true);
                } else {
                    FileUtils.moveFileToDirectory(fileFromDir, destFile, true);
                }
            } catch (FileExistsException e) {

                if (fileFromDir.isDirectory()) {
                    logger.warn("Directory '" + fileFromDir.getName() + "' already exists! Copying all files from it");
                    copyAllFilesFromDirectory(fileFromDir, new File(destFile.toString() + File.separator + fileFromDir.getName()));
                } else {
                    logger.warn("File '" + fileFromDir.getName() + "' already exists! Overwriting");
                    FileUtils.forceDelete(new File(destFile + "/" + fileFromDir.getName()));
                    FileUtils.moveFileToDirectory(fileFromDir, destFile, true);
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
