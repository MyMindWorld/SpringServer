package ru.protei.scriptServer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.protei.scriptServer.model.JsonScript;
import ru.protei.scriptServer.model.POJO.ResultToSelect;
import ru.protei.scriptServer.model.Parameters;
import ru.protei.scriptServer.model.Script;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.passay.DictionaryRule.ERROR_CODE;

@Component
public class Utils {
    Logger logger = LoggerFactory.getLogger(Utils.class);

    @Value("${scriptServerResourcesPath:/ScriptsConfig}")
    private String scriptServerResourcesPath;
    @Value("${configPath:/config}")
    private String configPath;
    @Value("${scriptsPath:/scripts}")
    private String scriptsPath;
    @Value("${venvPath:/venvDir}")
    private String venvPath;
    @Value("${requirementsPath:/requirements}")
    private String requirementsPath;
    @Value("${defaultVenvRequirementsFileName:defaultVenvRequirements.txt}")
    private String defaultVenvRequirementsFileName;
    @Value("${defaultVenvName:defaultVenv}")
    public String defaultVenvName;
    @Autowired
    private ResourceLoader resourceLoader;
    String webappFolder = "/src/main/webapp";  // todo Handle webaps folder

    public File getScriptsDirectory() {
        return new File(System.getProperty("user.dir") + webappFolder + scriptServerResourcesPath + scriptsPath + "/");
    }

    public File getVenvDirectory() {
        return new File(System.getProperty("user.dir") + webappFolder + scriptServerResourcesPath + venvPath + "/");
    }

    public File getRequirementsDirectory() {
        return new File(System.getProperty("user.dir") + webappFolder + scriptServerResourcesPath + requirementsPath + "/");
    }

    public String getPythonExecutable() {
        if (SystemUtils.IS_OS_LINUX) {
            return "python3";
        } else {
            return "python";
        }
    }

    public File getVenvActivationPath(String venvName) {
        if (SystemUtils.IS_OS_LINUX) {
            return new File(getVenvDirectory().toString());
        } else {
            return new File(getVenvDirectory().toString() + "\\" + venvName + "\\Scripts\\");
        }
    }

    public String[] getArgsForRequirementsInstall(File requirementsFile, String venvName) {
        if (SystemUtils.IS_OS_LINUX) {
            return new String[]{System.getProperty("user.dir") + webappFolder + scriptServerResourcesPath + venvPath + "/" + venvName + "/bin/python3", "-m", "pip", "install", "-r", requirementsFile.getAbsolutePath()};
        } else {
            return new String[]{"cmd", "/c", "activate.bat", "&&", "pip", "install", "-r", requirementsFile.getAbsolutePath()};
        }
    }

    public String[] getArgsForRunningScriptInVenv(String venvName, String scriptPath) {
        if (SystemUtils.IS_OS_LINUX) {
            return new String[]{System.getProperty("user.dir") + webappFolder + scriptServerResourcesPath + venvPath + "/" + venvName + "/bin/python3", "-u", scriptPath};
        } else {
            return new String[]{System.getProperty("user.dir") + webappFolder + scriptServerResourcesPath + venvPath + "/" + venvName + "/Scripts/python.exe ", "-u", scriptPath};
        }

    }

    @SneakyThrows
    public Map<String, List<String>> splitQuery(String params) {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        final String[] pairs = params.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }

    @SneakyThrows
    public String buildSelectQueryRun(String scriptQuery, String searchQuery, Map<String, List<String>> formData) {
        if (searchQuery == null) {
            searchQuery = "";
        }
        String resultRunString = scriptQuery;
        Pattern pattern = Pattern.compile("[${]+[-A-Za-zА-Яа-я0-9]+[}]");
        Matcher matcher = pattern.matcher(scriptQuery);
        while (matcher.find()) {
            if (matcher.group().equals("${searchSelect}")) {
                resultRunString = resultRunString.replace("${searchSelect}", "--searchSelect " + searchQuery);
            }
            String possibleParam = matcher.group().replace("${", "").replace("}", "");
            if (formData.keySet().contains(possibleParam)) {
                List<String> paramValuesList = formData.get(possibleParam);
                String paramValue = "";
                if (paramValuesList.size() == 1) {
                    paramValue = paramValuesList.get(0);
                } else {
                    paramValue = paramValuesList.toString();
                }
                if (paramValue == null) {
                    paramValue = "";
                }
                resultRunString = resultRunString.replace(matcher.group(), possibleParam + " " + paramValue);
            }
        }
        return resultRunString;
    }


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

    public String createResultsSelect2Json(ArrayList<String> scriptResults) {

        ResultToSelect resultObject = new ResultToSelect();
        ResultToSelect.Items[] itemsResult = new ResultToSelect.Items[scriptResults.size()];
        for (String result : scriptResults) {
            int resultIndex = scriptResults.indexOf(result);
            ResultToSelect.Items itemResult = new ResultToSelect.Items(result, resultIndex);
            itemsResult[resultIndex] = itemResult;
        }
        resultObject.setItems(itemsResult);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(resultObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Resource[] getConfigs() {
        try {
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(scriptServerResourcesPath + configPath + "/*.json");
            logger.info("Found : '" + resources.length + "' json configs");
            return resources;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File[] getVenvs() {
        String venvDir = new String();
        if (SystemUtils.IS_OS_LINUX) {
            venvDir = "./" + System.getProperty("user.dir") + webappFolder + scriptServerResourcesPath + venvPath + "/";
        } else {
            venvDir = System.getProperty("user.dir") + webappFolder + scriptServerResourcesPath + venvPath + "\\";
        }
        logger.info("Looking for venvs in '" + venvDir + "'");
        File[] directories = new File(venvDir).listFiles(File::isDirectory);
        logger.info("Found : '" + directories.length + "' venv's");
        return directories;
    }

    public File getDefaultVenvRequirements() {
        File defaultRequirements;

        try {
            defaultRequirements = new ClassPathResource(defaultVenvRequirementsFileName).getFile();
            logger.info("Found default requirements in : '" + defaultRequirements.getPath() + "'");
            return defaultRequirements;
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

    public String[] createParamsString(Script script, Map<String, String> params) {
        params.remove("scriptName"); // Аргумент в котором передается от клиента название скрипта
        // todo refactor to array mb?
        // todo нужна ли здесь валидация
        Parameters[] parametersKeys = stringToListOfParams(script.getParametersJson());
        ArrayList<String> resultArray = new ArrayList<String>();
        for (Parameters paramKey : parametersKeys) { // сбор констант
            if (paramKey.constant) {
//                if (paramKey.values) // продумать как назвать параметр для скрипта в конфиге
                if (params.get(paramKey) == null) {
                    continue;
                }
                resultArray.add(paramKey.getParam());
                resultArray.add(paramKey.getDefaultConstant());
            }
        }
        for (String paramKey : params.keySet()) { // сбор констант
            String paramValue = params.get(paramKey);
            if (paramValue == null) {
                continue;
            }
            else {
                resultArray.add(paramKey);
                resultArray.add(paramValue);
            }
//                resultArray.add(paramValue.get(0));
//            if (paramValue.size()==1){
//                resultArray.add(paramKey);
//                resultArray.add(paramValue.get(0));
//            }
//            else {
//                resultArray.add(paramKey);
//                resultArray.add(StringUtils.join(paramValue, " "));
//            }
        }
        String[] resultString = new String[resultArray.size()];
        for (int i = 0; i < resultArray.size(); i++) {
            resultString[i] = resultArray.get(i);
        }
        return resultString;
    }

}
