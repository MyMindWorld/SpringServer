package io.scriptServer.utils.SystemIntegration;

import io.scriptServer.controller.ScriptWebSocketController;
import io.scriptServer.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class DynamicParamsScriptsRunner {
    Logger logger = LoggerFactory.getLogger(DynamicParamsScriptsRunner.class);
    @Autowired
    Utils utils;

    @Autowired
    ScriptWebSocketController scriptWebSocketController;

    public Runstate runstate;
    public int processExitcode = -1;


    public ArrayList<String> run(String commandParams, File directory) {
        ArrayList<String> linesSoFarStdout = new ArrayList<>();
        ArrayList<String> linesSoFarStderr = new ArrayList<>();
        this.runstate = Runstate.RUNNING;
        // 1 start the process
        Process p = null;
        // todo build params refactor
        try {
            // pass the arguments directly during startup to the process
            // * example:
            // * run 'java -jar myexecutable.jar arg0 arg1 ...'
            String[] args = commandParams.split(" ");
            logger.info(Arrays.toString(args));
            ProcessBuilder pb = new ProcessBuilder(args).directory(directory);
            p = pb.start();

            // 2 print the output
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            InputStream eis = p.getErrorStream();
            BufferedReader ebr = new BufferedReader(new InputStreamReader(eis));


            String lineStdout = null;
            String lineStderr = null;
            while (p.isAlive()) {
                while ((lineStdout = br.readLine()) != null || (lineStderr = ebr.readLine()) != null) {
                    if (lineStdout != null & !lineStdout.isEmpty()) {
                        linesSoFarStdout.add(lineStdout);
                    }
                    if (lineStderr != null & !lineStdout.isEmpty()) {
                        logger.error(lineStderr);
                        linesSoFarStderr.add(lineStderr);
                    }
                }
            }
            // 3 when process ends
            this.processExitcode = p.exitValue();
        } catch (Exception e) {
            logger.error("Something went wrong!");
            logger.error(e.getMessage());
        }

        if (processExitcode != 0) {
            logger.error("The other process stopped with unexpected exitcode: " + processExitcode);
        }
        this.runstate = Runstate.STOPPED;
        linesSoFarStdout.addAll(linesSoFarStderr);
        logger.info("Result of script for select " + linesSoFarStdout);
        return linesSoFarStdout;
    }
}