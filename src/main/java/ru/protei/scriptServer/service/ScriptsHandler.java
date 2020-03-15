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
import ru.protei.scriptServer.utils.Async.AsyncExecutor;
import ru.protei.scriptServer.utils.Utils;

import java.io.*;
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
    Utils utils;


    @SneakyThrows
    public void updateScriptsInDb() {
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

        Role role_all = roleService.createRoleIfNotFound("ROLE_ALL_SCRIPTS", allPrivilege);
        if (role_all == null)
            roleService.updateRole("ROLE_ALL_SCRIPTS", allPrivilege);

        logger.warn(scriptRepository.findAll().toString());


    }

    @SneakyThrows
    public void runPythonScript(String[] params){
        logger.info(servletContextPath);
        logger.info("Requested  script");
        String executable = "python";
//        String[] commandParams = {"@ping -n 5 localhost","echo \"hello world\"","exit 123"};
//        String[] commandParams = {"import os","print(os.getcwd())","exit()"};
//        String[] commandParams = {"test.py"};
        logger.info(String.valueOf(utils.getScriptsDirectory().isDirectory()));
        AsyncExecutor asyncExecutor = new AsyncExecutor(executable, params,utils.getScriptsDirectory(), false);
        logger.info("x"+"/x\tsecs in main thread \t\t status:"+asyncExecutor.runstate+" of async thread that monitors the process");
        asyncExecutor.start();//start() invokes the run() method as a detached thread
//        Thread.sleep(1000);
//        asyncExecutor.terminate();
//        for(int i=0;i<10;i++) {
//            // you can do whatever here and the other process is still running and printing its output inside detached thread
//            Thread.sleep(10);
//            logger.info(i+"/10\tsecs in main thread \t\t status:"+asyncExecutor.runstate+" of async thread that monitors the process");
//        }
//
        asyncExecutor.join(); // main thread has nothing to do anymore, wait till other thread that monitor other process finishes as well
        logger.info("END OWN-PROGRAMM: 0 , END OTHER PROCESS:"+asyncExecutor.processExitcode);
//        System.exit(0);
        logger.info("Done.");

    }



}
