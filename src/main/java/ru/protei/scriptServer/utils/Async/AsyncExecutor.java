package ru.protei.scriptServer.utils.Async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.controller.ScriptsController;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class AsyncExecutor {
    Logger logger = LoggerFactory.getLogger(AsyncExecutor.class);

    @Autowired
    ScriptsController scriptsController;


    private String executable;
    private String scriptName;
    private String[] commandParams;
    private File directory;
    public ArrayList<String> linesSoFarStdout = new ArrayList<>();
    public ArrayList<String> linesSoFarStderr = new ArrayList<>();
    public Runstate runstate;
    public int processExitcode = -1;
    private boolean passCommandsAsLinesToShellExecutableAfterStartup = false;

    /**
     * if you want to run a single-process with arguments use <b>false</b> example executable="java" commandParams={"-jar","myjarfile.jar","arg0","arg1"}
     * <p>
     * if you want to run a shell-process and enter commands afterwards use <b>true</b> example executable="cmd" commandParams={"@ping -n 5 localhost","echo \"hello world\"","exit 123"}
     *
     * @param executable
     * @param commandParams
     * @param passCommandsAsLinesToShellExecutableAfterStartup
     */

    public void run(String executable, String[] commandParams, File directory, boolean passCommandsAsLinesToShellExecutableAfterStartup, String scriptName) {
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
                p = Runtime.getRuntime().exec(new String[]{executable}, null, directory);
                PrintWriter stdin = new PrintWriter(p.getOutputStream());
                for (int i = 0; i < commandParams.length; i++) {
                    String commandstring = commandParams[i];
                    stdin.println(commandstring);
                    scriptsController.sendToSock(commandstring);
                }
                stdin.close();
            } else {
                // pass the arguments directly during startup to the process
                // * example:
                // * run 'java -jar myexecutable.jar arg0 arg1 ...'
                String[] args = new String[]{executable, scriptName};
                String[] execWithArgs = new String[args.length + commandParams.length];
                System.arraycopy(args, 0, execWithArgs, 0, args.length);
                System.arraycopy(commandParams, 0, execWithArgs, args.length, commandParams.length);
                logger.info(Arrays.toString(execWithArgs));
                p = Runtime.getRuntime().exec(execWithArgs, null, directory);
            }
            // 2 print the output
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            InputStream eis = p.getErrorStream();
            BufferedReader ebr = new BufferedReader(new InputStreamReader(eis));

            String lineStdout = null;
            String lineStderr = null;
            while (p.isAlive()) {
                Thread.yield(); // *
                // * free cpu clock for other tasks on your PC! maybe even add thread.sleep(milliseconds) to free some more
                // * everytime this thread gets cpu clock it will try the following codeblock inside the while and yield afterwards for the next time it gets cpu-time from sheduler
                while ((lineStdout = br.readLine()) != null || (lineStderr = ebr.readLine()) != null) {
                    if (lineStdout != null) {
//                        System.out.println(lineStdout);
                        logger.info(lineStdout);
                        scriptsController.sendToSock(lineStdout);
                        linesSoFarStdout.add(lineStdout);
                    }
                    if (lineStderr != null) {
//                        System.out.println(lineStderr);
                        logger.error(lineStderr);
                        linesSoFarStderr.add(lineStderr);
                    }
                }
            }
            // 3 when process ends
            this.processExitcode = p.exitValue();
        } catch (Exception e) {
//            System.err.println("Something went wrong!");
            logger.error("Something went wrong!");
            e.printStackTrace();
        }
        if (processExitcode != 0) {
//            System.err.println("The other process stopped with unexpected existcode: " + processExitcode);
            logger.error("The other process stopped with unexpected existcode: " + processExitcode);
        }
        this.runstate = Runstate.STOPPED;
    }
}