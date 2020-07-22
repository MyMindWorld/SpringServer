package ru.protei.scriptServer.controller;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.protei.scriptServer.exception.StorageException;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.model.UserFile;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.StorageService;
import ru.protei.scriptServer.service.UserService;
import ru.protei.scriptServer.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Log
@RestController
public class FileUploadRestController {

    @Autowired
    StorageService storageService;
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    LogService logService;
    @Autowired
    UserService userService;
    @Autowired
    Utils utils;

    @GetMapping("/files/get_all")
    public ResponseEntity<?> listUploadedFiles() {

        return ResponseEntity.ok().body(storageService.getAllResourceFiles());
    }

    @SneakyThrows
    @RequestMapping(value = "/files/get_all_for_select", method = RequestMethod.GET)
    public String runScriptForSelect(String search, String scriptName) {
        if (search == null) {
            search = "";
        }
        log.info("search '" + search + "'");

        return utils.createResultsSelect2Json((ArrayList<String>) storageService.getAllResourceFiles(scriptName), null, search);
    }

    @GetMapping("/files/serve_file/{fileId}")
    @ResponseBody
    @SneakyThrows
    public ResponseEntity<Resource> serveFile(@PathVariable Long fileId) {

        Resource file = storageService.loadAsResource(fileId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @SneakyThrows
    @PostMapping("/files/upload_file/{scriptName}")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable String scriptName, HttpServletRequest req) {
        Script scriptFromDB = scriptRepository.findByNameEquals(scriptName);
        if (scriptFromDB == null) {
            throw new Exception("Valid Script should be specified!");
        }
        User user = userService.getUserByName(req.getRemoteUser());

        if (storageService.isFilePresent(file)) {
            if (!userService.checkPrivilege(user, "FILES_EDIT") || !userService.checkPrivilege(user, scriptName)) {
                logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "UPLOADING FILE WITHOUT ROLE!", file.getOriginalFilename());
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
            logService.logAction(user.getUsername(), req.getRemoteAddr(), "Update file", storageService.getUploadPath(file, scriptName).toString());
        } else {
            logService.logAction(user.getUsername(), req.getRemoteAddr(), "Add new file", storageService.getUploadPath(file, scriptName).toString());
        }
        storageService.uploadFile(file, user.getUsername(), scriptName);


        return ResponseEntity.ok().body("You successfully uploaded " + file.getOriginalFilename() + "!");
    }

    @PostMapping("/files/delete_file")
    @SneakyThrows
    public ResponseEntity<?> handleFileDelete(UserFile file, HttpServletRequest req) {
        UserFile userFile = storageService.findFileById(file.getId());
        if (userFile == null) {
            throw new Exception("Valid file should be specified!");
        }

        User user = userService.getUserByName(req.getRemoteUser());
        if (!userService.checkPrivilege(user, "FILES_EDIT")) {
            logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "REMOVING FILE WITHOUT ROLE!", file.getName());
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        logService.logAction(user.getUsername(), req.getRemoteAddr(), "Delete file", storageService.getUploadPath(file.getName(), file.getScript()).toString());

        storageService.deleteFile(userFile);


        return ResponseEntity.ok().body("You successfully deleted " + file.getName() + "!");
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageException exc) {
        return ResponseEntity.notFound().build();
    }

}