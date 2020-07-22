package ru.protei.scriptServer.controller;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.ScriptRepository;
import ru.protei.scriptServer.repository.UserFileRepository;
import ru.protei.scriptServer.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@Log
public class FileUploadController {

    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    UserFileRepository userFileRepository;
    @Autowired
    UserService userService;

    @RequestMapping("/files/control")
    public String testPage(Model model, HttpServletRequest req) {
        User user = userService.getUserByName(req.getRemoteUser());
        List<Script> allScripts = userService.getAllAvailableScriptsForUser(user);
        List<String> allScriptsAsString = userService.getAllAvailableScriptsForUserAsString(user);

        model.addAttribute("scripts", allScripts);
        model.addAttribute("files", userFileRepository.findAllByScriptIn(allScriptsAsString));

        return "userFiles";
    }
}
