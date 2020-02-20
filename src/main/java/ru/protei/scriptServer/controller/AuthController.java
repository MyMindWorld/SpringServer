package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @RequestMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        if (request.getParameter("error") != null){
            model.addAttribute("error",true);
        }

        return "loginNew";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    protected String doPost(HttpServletRequest request, HttpServletResponse response, Model model) throws ServletException, IOException {
        String username = request.getParameter("Username");
        String password = request.getParameter("pass");

       logger.warn(username);
       logger.warn(password);

       model.addAttribute("username", username);
       return "index";
    }

}
