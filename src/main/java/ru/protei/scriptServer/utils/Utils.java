package ru.protei.scriptServer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
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
import ru.protei.scriptServer.model.Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.passay.DictionaryRule.ERROR_CODE;

@Component
public class Utils {
    Logger logger = LoggerFactory.getLogger(Utils.class);

    @Value("${scriptsPath:aDefaultUrl}")
    private String scriptsPath;
    @Autowired
    private ResourceLoader resourceLoader;

    public Parameters[] stringToListOfParams(String source) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(source, Parameters[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonScript parseJsonToObject(InputStream source) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(source, JsonScript.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Resource[] getScriptsPath() {
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

    public static String parametersToPojo(Parameters parameters) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(parameters);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateSecurePassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        String password = gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
        return password;
    }


}
