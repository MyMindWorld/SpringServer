package ru.protei.scriptServer.controller;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.protei.scriptServer.exception.StorageException;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.StorageService;
import ru.protei.scriptServer.service.UserService;
import ru.protei.scriptServer.utils.Utils;

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

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageException exc) {
        return ResponseEntity.notFound().build();
    }

}