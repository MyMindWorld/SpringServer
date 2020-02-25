package ru.protei.scriptServer.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.protei.scriptServer.model.JsonScript;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Utils {
    Logger logger = LoggerFactory.getLogger(Utils.class);

    @Value( "${scriptsPath:aDefaultUrl}" )
    private String scriptsPath;
    @Autowired
    private ResourceLoader resourceLoader;

    public JsonScript parseJsonToObject(InputStream inputStream){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(inputStream, JsonScript.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Resource[] getScriptsPath(){
        final Path rootPath = Paths.get(scriptsPath);
        try {
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(scriptsPath + "/config/*.json");
            logger.info("Found : '" + resources.length + "' json configs");
            return resources;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }


}
