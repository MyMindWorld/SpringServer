package io.scriptServer.utils.SystemIntegration;

import io.scriptServer.config.MessageQueueConfig;
import io.scriptServer.config.ProcessQueueConfig;
import io.scriptServer.controller.ScriptWebSocketController;
import io.scriptServer.model.Enums.ModalType;
import io.scriptServer.model.Enums.ServiceMessage;
import io.scriptServer.model.Script;
import io.scriptServer.utils.Utils;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.scriptServer.model.POJO.Message;
import io.scriptServer.model.POJO.RunningScript;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PythonScriptsRunner extends Thread {
    Logger logger = LoggerFactory.getLogger(PythonScriptsRunner.class);
    @Autowired
    Utils utils;

    @Autowired
    ScriptWebSocketController scriptWebSocketController;
    @Autowired
    MessageQueueConfig queueConfig;
    @Autowired
    ProcessQueueConfig processQueueConfig;

    public ArrayList<String> linesSoFarStdout = new ArrayList<>();
    public ArrayList<String> linesSoFarStderr = new ArrayList<>();
    public Runstate runstate;
    public int processExitcode = -1;
    public Pattern userInputFlagPattern = Pattern.compile("##ScriptServer\\[.*]");
    public Pattern textForUserInModal = Pattern.compile("'(.*)'", Pattern.MULTILINE);
    public Pattern typeOfModal = Pattern.compile("\\[(.*?)\\'", Pattern.MULTILINE);
    public Process p = null;

    @SneakyThrows
    public void run(String[] commandParams, File directory, boolean passCommandsAsLinesToShellExecutableAfterStartup, Script script, String venvName, String username, String uniqueSessionId) {
        if (Thread.currentThread().isInterrupted()) {
            String msg = "Process won't start, thread was interrupted";
            logger.info(msg);
            scriptWebSocketController.sendToSockFromScript(username, msg, script.getName(), uniqueSessionId);
            return;
        }
        this.runstate = Runstate.RUNNING;
        // 1 start the process
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
                    scriptWebSocketController.sendToSockFromScript(username, commandstring, script.getName(), uniqueSessionId);
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
            logger.info("Adding process to blocking queue");
            RunningScript runningScript = new RunningScript();
            runningScript.setScriptName(script.getName());
            runningScript.setSessionId(uniqueSessionId);
            runningScript.setUserName(username);
            runningScript.setThreadName(Thread.currentThread().getName());
            processQueueConfig.processBlockingQueue().put(new HashMap<RunningScript, Process>() {{
                put(runningScript, p);
            }});
            logger.info("Starting reading from script");
            // 2 print the output
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, utils.getCharsetForSystem()));
            InputStream eis = p.getErrorStream();
            BufferedReader ebr = new BufferedReader(new InputStreamReader(eis, utils.getCharsetForSystem()));
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            String lineStdout = null;
            String lineStderr = null;

            while (p.isAlive()) {
                while ((lineStdout = br.readLine()) != null || (lineStderr = ebr.readLine()) != null) {
                    if (lineStdout != null) {
                        if (lineStdout.matches(userInputFlagPattern.toString())) {
                            logger.info("Caught user depending input!");
                            String userAnswer = handleInputFromUser(username, lineStdout, script.getName(), uniqueSessionId);
                            if (userAnswer == null) {
                                logger.error("Received null input, destroying");
                                p.destroy();

                            }
                            stdin.println(userAnswer);
                            stdin.flush();
                            logger.info("Sent text to script, continuing");
                        } else {
                            logger.info(lineStdout);
                            scriptWebSocketController.sendToSockFromScript(username, lineStdout, script.getName(), uniqueSessionId);
                            linesSoFarStdout.add(lineStdout);
                        }
                    }
                    if (lineStderr != null) {
                        logger.error(lineStderr);
                        scriptWebSocketController.sendToSockFromScript(username, lineStderr, script.getName(), uniqueSessionId);
                        linesSoFarStderr.add(lineStderr);
                    }
                    if (currentThread().isInterrupted()) {
                        logger.info("Caught interrupt in run thread");
                        scriptWebSocketController.sendToSockFromServer(username, "Process is stopping...", script.getName(), uniqueSessionId);
                        p.destroy();
                    }
                }
            }
            // 3 when process ends
            this.processExitcode = p.exitValue();
            if (processExitcode != 0) {
                scriptWebSocketController.sendToSockFromServerService(username, "Process ended with unexpected exitcode: " + processExitcode, script.getName(), uniqueSessionId, ServiceMessage.Stopped);
                logger.error("The other process stopped with unexpected exitcode: " + processExitcode);
            } else {
                scriptWebSocketController.sendToSockFromServerService(username, "Process ended successfully!", script.getName(), uniqueSessionId, ServiceMessage.Stopped);
            }
            this.runstate = Runstate.STOPPED;

            return;
        } catch (Exception e) {
            logger.error("Exception in script run");
            if (e.getMessage().contains("Stream closed")) { // Process was stopped manually
                scriptWebSocketController.sendToSockFromServer(username, "Process ended with exception: " + e.getMessage(), script.getName(), uniqueSessionId);
                return;
            }
            scriptWebSocketController.sendToSockFromServerService(username, "Process ended with exception: " + e.getMessage(), script.getName(), uniqueSessionId, ServiceMessage.Stopped);
            return;
        }
    }

    @SneakyThrows
    public String handleInputFromUser(String username, String lineFromPython, String scriptName, String uniqueSessionId) {
        try {
            Message msg = null;
            Matcher userInputFlagMatcher = userInputFlagPattern.matcher(lineFromPython);
            Matcher textForUserInModalMatcher = textForUserInModal.matcher(lineFromPython);
            Matcher typeOfModalMatcher = typeOfModal.matcher(lineFromPython);
            userInputFlagMatcher.find();
            textForUserInModalMatcher.find();
            typeOfModalMatcher.find();

            String textForUser = textForUserInModalMatcher.group(1);
            ModalType typeOfModalToShow = ModalType.valueOf(typeOfModalMatcher.group(1));

            scriptWebSocketController.sendToSockFromServer(username, textForUser, scriptName, typeOfModalToShow, uniqueSessionId);
            if (typeOfModalToShow.equals(ModalType.ShowInfo)) {
                return "";// Script is not waiting anything
            }
            logger.info("Waiting for user reaction on session id '" + uniqueSessionId + "'");

            while (!((msg = queueConfig.blockingQueue().take()).getUsername().equals(username)
                    & msg.getScriptName().equals(scriptName)
                    & msg.getUniqueSessionId().equals(uniqueSessionId))) {
                queueConfig.blockingQueue().put(msg);
                // todo ping if client is in script still
                logger.debug(msg.toString());
                if (currentThread().isInterrupted()) {
                    logger.info("Caught interrupt in waiting input thread");
                    return null;
                }
            }
            logger.info("Returning to script '" + msg.getText() + "'");
            return msg.getText();
        } catch (Throwable e) {
            scriptWebSocketController.sendToSockFromServer(username, "Exception during input handling '" + e.getCause() + "'", scriptName, uniqueSessionId);
            return null;
        }
    }

}