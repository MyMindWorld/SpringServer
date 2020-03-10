package ru.protei.scriptServer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.protei.scriptServer.service.LogService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

@ControllerAdvice
//@Controller
public class ExceptionHandlingController {
    @Autowired
    LogService logService;
    Logger logger = LoggerFactory.getLogger(ExceptionHandlingController.class);

    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public String AccessException(HttpServletRequest req, Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        logService.logAction(req.getRemoteUser(),req.getRemoteAddr(),req.getRequestURL().toString(), req.getMethod(),sw.toString());
        return "ErrorCodes/500";
    }
    @ExceptionHandler({ConstraintViolationException.class, org.hibernate.exception.ConstraintViolationException.class,org.springframework.transaction.TransactionSystemException.class})
    public String databaseError(HttpServletRequest req, Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        logService.logAction(req.getRemoteUser(),req.getRemoteAddr(),req.getRequestURL().toString(), req.getMethod(),sw.toString());
        return "ErrorCodes/500";
    }
}
