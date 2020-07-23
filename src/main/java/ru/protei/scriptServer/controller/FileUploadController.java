package ru.protei.scriptServer.controller;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.model.UserFile;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.repository.UserFileRepository;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.StorageService;
import ru.protei.scriptServer.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@Log
public class FileUploadController {

    @Autowired
    ErrorPagesController errorPagesController;
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    UserFileRepository userFileRepository;
    @Autowired
    StorageService storageService;
    @Autowired
    LogService logService;
    @Autowired
    UserService userService;

    @RequestMapping("/files/control")
    public String filesPage(Model model, HttpServletRequest req) {
        User user = userService.getUserByName(req.getRemoteUser());
        List<Script> allScripts = userService.getAllAvailableScriptsForUser(user);
        List<String> allScriptsAsString = userService.getAllAvailableScriptsForUserAsString(user);

        model.addAttribute("scripts", allScripts);
        model.addAttribute("files", userFileRepository.findAllByScriptIn(allScriptsAsString));

        return "userFiles";
    }

    @SneakyThrows
    @PostMapping("/files/upload_file/{scriptName}")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable String scriptName, Model model, HttpServletRequest req) {
        Script scriptFromDB = scriptRepository.findByNameEquals(scriptName);
        if (scriptFromDB == null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Valid Script should be specified!");
            return filesPage(model, req);
        }
        User user = userService.getUserByName(req.getRemoteUser());

        if (storageService.isFilePresent(file, scriptName)) {
            if (!userService.checkPrivilege(user, "FILES_EDIT") || !userService.checkPrivilege(user, scriptName)) {
                logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "UPLOADING FILE WITHOUT ROLE!", file.getOriginalFilename());
                return errorPagesController.Forbidden();
            }
            logService.logAction(user.getUsername(), req.getRemoteAddr(), "Update file", storageService.getUploadPath(file, scriptName).toString());
        } else {
            logService.logAction(user.getUsername(), req.getRemoteAddr(), "Add new file", storageService.getUploadPath(file, scriptName).toString());
        }
        storageService.uploadFile(file, user.getUsername(), scriptName);


        model.addAttribute("success", true);
        model.addAttribute("successMessage", "You successfully uploaded " + file.getOriginalFilename() + "!");
        return filesPage(model, req);
    }

    @PostMapping("/files/delete_file")
    @SneakyThrows
    public String handleFileDelete(UserFile file, Model model, HttpServletRequest req) {
        UserFile userFile = storageService.findFileById(file.getId());
        if (userFile == null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Valid file should be specified!");
            return filesPage(model, req);
        }

        User user = userService.getUserByName(req.getRemoteUser());
        if (!userService.checkPrivilege(user, "FILES_EDIT")) {
            logService.logAction(req.getRemoteUser(), req.getRemoteAddr(), "REMOVING FILE WITHOUT ROLE!", file.getName());
            return errorPagesController.Forbidden();
        }

        logService.logAction(user.getUsername(), req.getRemoteAddr(), "Delete file", storageService.getUploadPath(file.getName(), userFile.getScript()).toString());

        storageService.deleteFile(userFile);

        model.addAttribute("success", true);
        model.addAttribute("successMessage", "You successfully deleted " + file.getName() + "!");
        return filesPage(model, req);
    }
}
