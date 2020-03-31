package ru.protei.scriptServer.utils.SystemIntegration;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.Venv;
import ru.protei.scriptServer.repository.VenvRepository;
import ru.protei.scriptServer.utils.Utils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.List;

@Service
public class VenvManager {
    Logger logger = LoggerFactory.getLogger(VenvManager.class);
    @Autowired
    Utils utils;
    @Autowired
    VenvRepository venvRepository;

    @SneakyThrows
    public Venv createIfNotExists(String name, String requirementsFileName) {
        File venvDirectory = new File(utils.getVenvDirectory().toString() + "\\" + name);
        Venv venvFromRepo = venvRepository.findByNameEquals(name);
        if (!venvDirectory.exists()) {
            if (venvFromRepo != null) {
                venvRepository.delete(venvFromRepo);
            }
            return createVenv(name, requirementsFileName);
        } else {

            if (venvFromRepo == null) {
                deleteVenv(name);
                return createVenv(name, requirementsFileName);
            } else {
                return venvFromRepo;
            }
        }

    }

    @SneakyThrows
    public Venv createVenv(String name, String requirementsFileName) {

        Venv venv = Venv.builder().name(name).build();

        logger.info("Started venv '" + name + "' creation");
        Process venvCreatingProc = Runtime.getRuntime().exec(new String[]{utils.getPythonExecutable(), "-m", "venv", name}, null, utils.getVenvDirectory());
        InputStream is = venvCreatingProc.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        InputStream eis = venvCreatingProc.getErrorStream();
        BufferedReader ebr = new BufferedReader(new InputStreamReader(eis));
        String lineStdout = null;
        String lineStderr = null;

        while ((lineStdout = br.readLine()) != null || (lineStderr = ebr.readLine()) != null) {
            if (lineStdout != null) {
                logger.info(lineStdout);
            }
            if (lineStderr != null) {
                logger.error(lineStderr);
            }
        }
        logger.info("Finished venv '" + name + "' creation");
        venvRepository.save(venv);
        installPackagesInVenv(venv, requirementsFileName);
        return venv;
    }

    @SneakyThrows
    public Venv installPackagesInVenv(Venv venv, String requirementsFileName) {
        logger.info("Started venv '" + venv.getName() + "' packages from '" + requirementsFileName + "' install");

        File requirementsFile = new File(utils.getRequirementsDirectory() + "/" + requirementsFileName);
        List<String> installedPackages = FileUtils.readLines(requirementsFile, "utf-8");

        Process venvCreatingProc = Runtime.getRuntime().exec(utils.getArgsForRequirementsInstall(requirementsFile), null, utils.getVenvActivationPath(venv.getName()));
        InputStream is = venvCreatingProc.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        InputStream eis = venvCreatingProc.getErrorStream();
        BufferedReader ebr = new BufferedReader(new InputStreamReader(eis));
        String lineStdout = null;
        String lineStderr = null;

        while ((lineStdout = br.readLine()) != null || (lineStderr = ebr.readLine()) != null) {
            if (lineStdout != null) {
                logger.info(lineStdout);
            }
            if (lineStderr != null) {
                logger.error(lineStderr);
            }
        }
        logger.info("Finished venv packages install");
        venv.setInstalledPackages(installedPackages);
        venvRepository.save(venv);
        return venv;
    }

    @SneakyThrows
    public void deleteVenv(String venvName) {
        File dir = new File(utils.getVenvDirectory().toString() + "\\" + venvName);
        if (!dir.canWrite()) {
            logger.error("Directory with venv is not writable! Change permissions");
            return;
        }
        if (!dir.isDirectory() || !dir.exists()) {
            logger.error("'Directory' with venv :'" + venvName + "' is not a directory or doesn't exists!");
            return;
        }
        logger.info("Started venv '" + venvName + "' deleting");
        FileUtils.deleteDirectory(dir);
        venvRepository.delete(venvRepository.findByNameEquals(venvName));
        logger.info("Finished venv '" + venvName + "' deleting");
    }

    @SneakyThrows
    public void deleteVenv(Venv venv) {
        deleteVenv(venv.getName());
    }
}
