package io.scriptServer.service;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import io.scriptServer.model.UserFile;
import io.scriptServer.repository.UserFileRepository;
import io.scriptServer.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;


@Service
@Log
public class StorageService {

    @Autowired
    Utils utils;
    @Autowired
    LogService logService;

    @Autowired
    UserFileRepository userFileRepository;

    public UserFile findFileById(Long id) {
        return userFileRepository.findByIdEquals(id);
    }

    public boolean isFilePresent(MultipartFile file, String scriptName) {
        Path copyLocation = Paths
                .get(utils.getUserResourcesDir() + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));

        return userFileRepository.findByFullPathEquals(getUploadPath(file, scriptName).toString()) != null;
    }

    public Path getUploadPath(MultipartFile file) {
        return getUploadPath(file.getOriginalFilename());
    }

    public Path getUploadPath(MultipartFile file, String script) {
        return getUploadPath(file.getOriginalFilename(), script);
    }

    public Path getUploadPath(String file, String script) {
        return Paths
                .get(utils.getUserResourcesDir() + File.separator + script + File.separator + StringUtils.cleanPath(file));
    }

    public Path getUploadPath(String file) {
        return Paths
                .get(utils.getUserResourcesDir() + File.separator + StringUtils.cleanPath(file));
    }

    public void uploadFile(MultipartFile file, String user, String script) throws Exception {
        if (file.isEmpty()) {
            log.warning("Empty!");
            throw new Exception("Failed to store empty file");
        }
        try {
            Path copyLocation = getUploadPath(file, script);
            utils.createFolderIfNotExists(copyLocation.toFile());
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

            UserFile userFile = userFileRepository.findByFullPathEquals(copyLocation.toString());
            if (userFile == null) {
                userFile = UserFile.builder()
                        .name(file.getOriginalFilename())
                        .fullPath(copyLocation.toString())
                        .lastModified(new Date())
                        .lastModifiedBy(user)
                        .script(script)
                        .build();

            } else {
                userFile.setLastModified(new Date());
                userFile.setLastModifiedBy(user);
            }

            userFileRepository.save(userFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
    }

    public void deleteFile(UserFile userFile) throws IOException {
        FileUtils.forceDelete(getUploadPath(userFile.getName(), userFile.getScript()).toFile());
        userFileRepository.delete(userFile);
    }

    public Resource loadAsResource(Long fileId) throws IOException {
        UserFile userFile = userFileRepository.findByIdEquals(fileId);


        Resource file = new PathResource(userFile.getFullPath());
        return file;
    }


    @SneakyThrows
    public List<String> getAllResourceFiles() {
        return utils.getFilesListFromFolder(utils.getUserResourcesDir());
    }

    @SneakyThrows
    public List<String> getAllResourceFiles(String scriptName) {
        return utils.getFilesListFromFolder(new File(utils.getUserResourcesDir().toString() + File.separator + scriptName + File.separator));
    }

    @SneakyThrows
    public String findFileForScriptWithName(String fileName, String scriptName) {
        return userFileRepository.findByNameEqualsAndScriptEquals(fileName, scriptName).getFullPath();
    }
}