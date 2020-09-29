package ru.protei.scriptServer.service;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.model.Venv;
import ru.protei.scriptServer.repository.VenvRepository;
import ru.protei.scriptServer.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class VenvService {
    Logger logger = LoggerFactory.getLogger(VenvService.class);
    @Autowired
    Utils utils;
    @Autowired
    VenvRepository venvRepository;

    @SneakyThrows
    public Venv createDefaultVenv() {
        File defaultRequirements = utils.getDefaultVenvRequirements();

        if (defaultRequirements == null) {
            logger.info("Default requirements not found.");
            Venv createdVenv = createIfNotExists(utils.defaultVenvName, null);
            return createdVenv;
        } else {
            logger.info("Default requirements found.");
            File destination = new File(utils.getRequirementsDirectory().toString() + "/");
            logger.info("Copying to '" + destination + "'");
            try {
                FileUtils.deleteQuietly(new File(utils.getRequirementsDirectory().toString() + "/" + defaultRequirements));
                FileUtils.copyFileToDirectory(defaultRequirements, destination);
                logger.info("Copying was successful.");
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        Venv createdVenv = createIfNotExists(utils.defaultVenvName, defaultRequirements.getName());
        if (defaultRequirements != null) {
            installPackagesInVenv(createdVenv, defaultRequirements.getName());
        }
        return createdVenv;
    }

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
        if (requirementsFileName != null) {
            installPackagesInVenv(venv, requirementsFileName);
        } else {
            logger.info("Venv doesn't require packages. Skipping");
        }

        return venv;
    }

    @SneakyThrows
    public Venv installPackagesInVenv(Venv venv, String requirementsFileName) {
        logger.info("Started venv '" + venv.getName() + "' packages from '" + requirementsFileName + "' install");
        File requirementsFile;
        List<String> installedPackages = new ArrayList<>();
        try {
            requirementsFile = new File(utils.getRequirementsDirectory() + "/" + requirementsFileName);
            FileUtils.readLines(requirementsFile, "utf-8");
        } catch (FileNotFoundException e) {
            logger.error("Requirements '" + requirementsFileName + "' not found");
            return null;
        }

//        TODO pip install -r requirements.txt --proxy=<ПРОТЕЙ_АНДРЕЙ> если тачка будет изолирована
//        https://wiki.protei.ru/doku.php?id=protei:qa:python:pypi&s[]=pip
//        https://wiki.protei.ru/doku.php?id=protei:qa:python:virtualenv&s[]=pip

        Process venvCreatingProc = Runtime.getRuntime().exec(utils.getArgsForRequirementsInstall(requirementsFile, venv.getName()), null, utils.getVenvActivationPath(venv.getName()));
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
        File dir = new File(utils.getVenvDirectory().toString() + "/" + venvName);
        if (!dir.isDirectory() || !dir.exists()) {
            logger.error("'Directory' with venv :'" + venvName + "' is not a directory or doesn't exists!");
            return;
        }
        if (!dir.canWrite()) {
            logger.error("Directory with venv is not writable! Change permissions");
            return;
        }
        logger.info("Started venv '" + venvName + "' deleting");
        FileUtils.deleteDirectory(dir);
        Venv venvFromRepository = venvRepository.findByNameEquals(venvName);
        if (venvFromRepository != null) {
            venvRepository.delete(venvFromRepository);
        }
        logger.info("Finished venv '" + venvName + "' deleting");
    }

    @SneakyThrows
    public void deleteAllVenvs() {
        File[] allVenvs = utils.getVenvs();
        for (File venv : allVenvs) {
            String venvName = venv.getName();
            deleteVenv(venvName);
        }

    }

    @SneakyThrows
    public void checkVenv(Script script, Venv venv) {
        File[] allVenvs = utils.getVenvs();
        for (File venvFromFs : allVenvs) {
            String venvName = venvFromFs.getName();
            if (venv.getName().equals(venvName)) {
                return;
            }
        }
        // If we are here - venv was not found on FS
        logger.warn("Venv was not found on FS! Recreating");
        venvRepository.delete(venv);
        createVenv(venv.getName(), script.getRequirements());
        logger.info("Venv was recreated!");
    }

    @SneakyThrows
    public void deleteVenv(Venv venv) {
        deleteVenv(venv.getName());
    }
}
