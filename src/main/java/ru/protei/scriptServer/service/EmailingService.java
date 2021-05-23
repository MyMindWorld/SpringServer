package ru.protei.scriptServer.service;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Service
@Log4j2
public class EmailingService {
    @Qualifier("messageSource") // todo verify qualifier
    @Autowired
    private MessageSource messages;

    @Autowired
    private Environment env;

    @Value("${email.sendFrom}")
    private String sendEmailsFrom;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    Utils utils;

    public void sendInviteUserEmail(User user, HttpServletRequest request) {
        try {
            mailSender.send(constructInviteEmail(utils.getAppUrl(request),
                    request.getLocale(), user));
        } catch (Exception e) {
            log.error("Sending invite email to '" + user.getEmail() + "' failed!", e);
        }
    }

    public void sendResetPasswordEmailWithResetToken(User user, String token, HttpServletRequest request) {
        try {
            mailSender.send(constructResetTokenEmail(utils.getAppUrl(request),
                    request.getLocale(), token, user));
        } catch (Exception e) {
            log.error("Sending invite email to '" + user.getEmail() + "' failed!", e);
        }
    }

    private SimpleMailMessage constructInviteEmail(
            String contextPath, Locale locale, User user) {
        String url = contextPath + "/login";
        String message = messages.getMessage("message.inviteToScriptServer",
                null, locale);
        return constructEmail(message, message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructResetTokenEmail(
            String contextPath, Locale locale, String token, User user) {
        String url = contextPath + "/user/changePassword?token=" + token;
        String message = messages.getMessage("message.resetPassword",
                null, locale);
        return constructEmail(message, message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body,
                                             User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(sendEmailsFrom);
        return email;
    }
}
