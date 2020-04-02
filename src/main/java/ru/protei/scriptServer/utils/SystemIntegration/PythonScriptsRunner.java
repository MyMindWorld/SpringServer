package ru.protei.scriptServer.utils.SystemIntegration;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.controller.ScriptWebSocketController;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class PythonScriptsRunner {
    Logger logger = LoggerFactory.getLogger(PythonScriptsRunner.class);
    @Autowired
    Utils utils;

    @Autowired
    ScriptWebSocketController scriptWebSocketController;

    public ArrayList<String> linesSoFarStdout = new ArrayList<>();
    public ArrayList<String> linesSoFarStderr = new ArrayList<>();
    public Runstate runstate;
    public int processExitcode = -1;


    public void run(String[] commandParams, File directory, boolean passCommandsAsLinesToShellExecutableAfterStartup, Script script, String venvName, String username) {
        this.runstate = Runstate.RUNNING;
        // 1 start the process
        Process p = null;
        // todo build params refactor
        try {
            if (passCommandsAsLinesToShellExecutableAfterStartup) {
                // open a shell-like process like cmd and pass the arguments/command after opening it
                // * example:
                // * open 'cmd' (shell)
                // * write 'echo "hello world"' and press enter
                ProcessBuilder pb = new ProcessBuilder(utils.getArgsForRunningScriptInVenv(venvName, script.getScript_path())).directory(directory);
                p = pb.start();
                PrintWriter stdin = new PrintWriter(p.getOutputStream());
                for (int i = 0; i < commandParams.length; i++) {
                    String commandstring = commandParams[i];
                    stdin.println(commandstring);
                    scriptWebSocketController.sendToSock(username, commandstring, script.getName());
                }
                stdin.close();
            } else {
                // pass the arguments directly during startup to the process
                // * example:
                // * run 'java -jar myexecutable.jar arg0 arg1 ...'
                String[] args = utils.getArgsForRunningScriptInVenv(venvName, script.getScript_path());
                String[] execWithArgs = new String[args.length + commandParams.length];
                System.arraycopy(args, 0, execWithArgs, 0, args.length);
                System.arraycopy(commandParams, 0, execWithArgs, args.length, commandParams.length);
                logger.info(Arrays.toString(execWithArgs));
                ProcessBuilder pb = new ProcessBuilder(execWithArgs).directory(directory);
                p = pb.start();
            }
            logger.info("READING START");
            // 2 print the output
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            InputStream eis = p.getErrorStream();
            BufferedReader ebr = new BufferedReader(new InputStreamReader(eis));


            String lineStdout = null;
            String lineStderr = null;
            while (p.isAlive()) {
                while ((lineStdout = br.readLine()) != null || (lineStderr = ebr.readLine()) != null) {
                    if (lineStdout != null) {
                        logger.info(lineStdout);
                        scriptWebSocketController.sendToSock(username,lineStdout,script.getName());
                        linesSoFarStdout.add(lineStdout);
                    }
                    else {
                        logger.info("NOTHING");
                    }
                    if (lineStderr != null) {
                        logger.error(lineStderr);
                        scriptWebSocketController.sendToSock(username,lineStderr,script.getName());
                        linesSoFarStderr.add(lineStderr);
                    }
                }
            }
            // 3 when process ends
            this.processExitcode = p.exitValue();
        } catch (Exception e) {
            logger.error("Something went wrong!");
            e.printStackTrace();
        }
        if (processExitcode != 0) {
            logger.error("The other process stopped with unexpected exitcode: " + processExitcode);
        }
        this.runstate = Runstate.STOPPED;
    }
}