package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @RequestMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        if (request.getParameter("error") != null){
            model.addAttribute("error",true);
        }

        return "login";
    }
}
