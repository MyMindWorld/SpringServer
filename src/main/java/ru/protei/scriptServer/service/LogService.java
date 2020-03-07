package ru.protei.scriptServer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.LogEntity;
import ru.protei.scriptServer.repository.LogRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class LogService {
    Logger logger = LoggerFactory.getLogger(LogService.class);
    @Autowired
    LogRepository logRepository;

    public void logAction(String triggeredBy, String ip, String action, String params) {
        logActionStore(triggeredBy, ip, action, params);

    }

    public void logAction(String triggeredBy, String ip, String action, String params, String errorLog) {
        logActionStore(triggeredBy, ip, action, params, errorLog);

    }

    private void logActionStore(String... strings) {
        Date date = new Date();
        LogEntity logEntity = new LogEntity();
        logEntity.setTriggeredBy(strings[0]);
        logEntity.setIp(strings[1]);
        logEntity.setAction(strings[2]);
        logEntity.setParams(strings[3]);
        logEntity.setDate(date);
        try {

            logEntity.setErrorLog(strings[4]);
        } catch (IndexOutOfBoundsException ignored) {
        }
        logger.info("Logging action '" + logEntity.toString() + '"');

        logRepository.save(logEntity);
    }
}
