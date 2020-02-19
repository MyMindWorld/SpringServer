package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.UserRepository;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.scriptServer.Utils.getUsername;


@Controller
public class MainPageController {
    Logger logger = LoggerFactory.getLogger(MainPageController.class);
    @Autowired
    UserRepository userRepository;

    @RequestMapping("/")
    public String redirectToIndex(ModelMap model) {
        return "index";
    }

    @RequestMapping("/index")
    public String showMenu(ModelMap model) {
        return "index";
    }

    @RequestMapping("/userlist")
    public String userList(ModelMap model) {
        List<User> userList = userRepository.findAll();
//        userList.sort(User::compareTo);
        model.addAttribute("users", userList);

        return "userlist";
    }

    @RequestMapping(value = "/useradd", method = RequestMethod.GET)
    public ModelAndView addUser() {
        return new ModelAndView("useradd", "command", new User());
    }

    @RequestMapping(value = "/useradd", method = RequestMethod.POST)
    public ModelAndView addUser(User user,
                                ModelMap model) {
        try {
            userRepository.save(user);
        } catch (org.springframework.transaction.TransactionSystemException ex) {
            logger.error(String.valueOf(ex));
            return new ModelAndView("useradd", "command", new User()).addObject("alert", true);
        }

        List<User> userList = userRepository.findAll();
//        userList.sort(User::compareTo);

        return new ModelAndView("userlist").addObject("users", userList);
    }

    @RequestMapping(value = "/userdelete", method = RequestMethod.GET)
    public ModelAndView deleteUserInit(Model model, HttpServletRequest request) {
        List<User> userList = userRepository.findAll();
//        userList.sort(User::compareTo);
        return new ModelAndView("userdelete").addObject("users", userList);
    }

    @RequestMapping(value = "/userdelete", method = RequestMethod.POST)
    public ModelAndView deleteUser(Model model, HttpServletRequest request) {
        List<User> userList = userRepository.findAll();
//        userList.sort(User::compareTo);
        long devId = Long.parseLong(request.getParameter("id"));
        if (devId != 0) {

            String username = getUsername();
            User userToDelete = userRepository.getOne(devId);
            logger.warn("Removing user '" + userToDelete + " by request of '" + username + "'");
            userRepository.delete(userToDelete);
            userList.remove(userToDelete);
            return new ModelAndView("userdelete").addObject("users", userList);
        }

        return new ModelAndView("userdelete").addObject("users", userList);
    }

}
