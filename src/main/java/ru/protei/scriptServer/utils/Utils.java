package ru.protei.scriptServer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.protei.scriptServer.config.ProcessQueueConfig;
import ru.protei.scriptServer.model.POJO.JsonScript;
import ru.protei.scriptServer.model.POJO.ResultToSelect;
import ru.protei.scriptServer.model.POJO.RunningScript;
import ru.protei.scriptServer.model.POJO.Parameters;
import ru.protei.scriptServer.model.Script;
import ru.protei.scriptServer.service.StorageService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    @Value("${repoFromGitDownlPath:/gitReposSource}")
    private String repoFromGitDownlPath;
    @Value("${defaultVenvRequirementsFileName:defaultVenvRequirements.txt}")
    private String defaultVenvRequirementsFileName;
    @Value("${defaultVenvName:defaultVenv}")
    public String defaultVenvName;
    @Value("${TomcatPath:NotSet}")
    public String tomcatPath;
    @Value("${upload.path:/userResources}")
    private String userResourcesDir;
    @Autowired
    private ProcessQueueConfig processQueueConfig;
    @Autowired
    private StorageService storageService;

    @PostConstruct
    public void UtilsCreation() {
        if (tomcatPath.equals("NotSet")) {
            logger.info("Tomcat path not found, using system property user.dir");
            tomcatPath = System.getProperty("user.dir") + webappFolder;
        } else {
            logger.info("Tomcat path found!");
            logger.info("Now it set to : '" + tomcatPath + "'");
        }

        assert !tomcatPath.equals("NotSet");
    }

    public void createDefaultFolders() {
        logger.info("Creating default folders");
        createFolderIfNotExists(getRequirementsDirectory());
        createFolderIfNotExists(getScriptsDirectory());
        createFolderIfNotExists(getVenvDirectory());
        createFolderIfNotExists(getConfigDirectory());
        createFolderIfNotExists(getUserResourcesDir());
        logger.info("Creation complete!");
    }

    public List<String> getFilesListFromFolder(File folder) {
        List<String> files = new ArrayList<>();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                files.addAll(getFilesListFromFolder(fileEntry));
            } else {
                files.add(fileEntry.getName());
            }
        }
        return files;
    }

    String webappFolder = "/src/main/webapp";


    public void createFolderIfNotExists(File folder) {
        if (folder.exists()) {
            return;
        } else {
            try {
                FileUtils.forceMkdir(folder);
            } catch (IOException e) {
                logger.error("Error during attempt of creation folder " + folder.getName());
                logger.error(e.getMessage());
            }

        }

    }

    public File getUserResourcesDir() {
        return new File(tomcatPath + scriptServerResourcesPath + userResourcesDir + "/");
    }

    public File getScriptsDirectory() {
        return new File(tomcatPath + scriptServerResourcesPath + scriptsPath + "/");
    }

    public File getVenvDirectory() {
        return new File(tomcatPath + scriptServerResourcesPath + venvPath + "/");
    }

    public File getConfigDirectory() {
        return new File(tomcatPath + scriptServerResourcesPath + configPath + "/");
    }

    public File getScriptServerResourcesPath() {
        return new File(tomcatPath + scriptServerResourcesPath + "/");
    }

    @SneakyThrows
    public File getFolderForScriptsFromGit(String scriptsRepoName) {
        File scriptFolder = new File(tomcatPath + scriptServerResourcesPath + repoFromGitDownlPath + "/" + scriptsRepoName + "/");
        if (scriptFolder.exists()) {
            try {
                FileUtils.deleteDirectory(scriptFolder);
            } catch (IOException e) {
                logger.error("Error during removing directory for scripts from '" + scriptsRepoName + "'");
                logger.error(e.getMessage());
            }

        }
        scriptFolder.mkdir();
        return scriptFolder;
    }

    public File getRequirementsDirectory() {
        return new File(tomcatPath + scriptServerResourcesPath + requirementsPath + "/");
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
            return new String[]{tomcatPath + scriptServerResourcesPath + venvPath + "/" + venvName + "/bin/python3", "-m", "pip", "install", "-r", requirementsFile.getAbsolutePath()};
        } else {
            return new String[]{"cmd", "/c", "activate.bat", "&&", "pip", "install", "-r", requirementsFile.getAbsolutePath()};
        }
    }

    public String[] getArgsForRunningScriptInVenv(String venvName, String scriptPath) {
        if (SystemUtils.IS_OS_LINUX) {
            return new String[]{tomcatPath + scriptServerResourcesPath + venvPath + "/" + venvName + "/bin/python3", "-u", scriptPath};
        } else {
            return new String[]{tomcatPath + scriptServerResourcesPath + venvPath + "/" + venvName + "/Scripts/python.exe ", "-u", scriptPath};
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

    public String createResultsSelect2Json(ArrayList<String> scriptResults, Parameters param, String search) {
        ResultToSelect resultObject = new ResultToSelect();
        int resultIndex = 0;
        ResultToSelect.Items[] itemsResult;
        if (param == null ? false : param.getValues() != null) { // Other params might be not initialized or present at all
            itemsResult = new ResultToSelect.Items[scriptResults.size() + param.getValues().length];
            for (String result : scriptResults) {
                resultIndex = scriptResults.indexOf(result);
                ResultToSelect.Items itemResult = new ResultToSelect.Items(result, result);
                itemsResult[resultIndex] = itemResult;
            }
            int defaultParamIndex = resultIndex;
            for (String defaultParam : param.values) {
                defaultParamIndex++;
                ResultToSelect.Items itemResult = new ResultToSelect.Items(defaultParam, defaultParam);
                itemsResult[defaultParamIndex] = itemResult;
            }
        } else {
            itemsResult = new ResultToSelect.Items[scriptResults.size()];
            for (String result : scriptResults) {
                resultIndex = scriptResults.indexOf(result);
                ResultToSelect.Items itemResult = new ResultToSelect.Items(result, result);
                itemsResult[resultIndex] = itemResult;
            }
        }
        // Filter results by search query from select
        List<ResultToSelect.Items> result =
                Arrays.stream(itemsResult)
                        .filter(element ->
                                element.getResultValue()
                                        .contains(search))
                        .collect(Collectors.toList());
        itemsResult = new ResultToSelect.Items[result.size()];
        itemsResult = result.toArray(itemsResult);

        resultObject.setItems(itemsResult);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(resultObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File[] getConfigs() {
        File dir = getConfigDirectory();
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".json");
            }
        });

        logger.info("Found : '" + files.length + "' json configs");
        return files;

    }

    public String getCharsetForSystem() {
        if (SystemUtils.IS_OS_LINUX) {
            return "utf-8";
        } else {
            return "windows-1251";
        }
    }

    public File[] getVenvs() {
        String venvDir = new String();
        if (SystemUtils.IS_OS_LINUX) {
            venvDir = tomcatPath + scriptServerResourcesPath + venvPath + "/";
        } else {
            venvDir = tomcatPath + scriptServerResourcesPath + venvPath + "\\";
        }
        logger.info("Looking for venvs in '" + venvDir + "'");
        File[] directories = new File(venvDir).listFiles(File::isDirectory);
        logger.info("Found : '" + directories.length + "' venv's");
        return directories;
    }

    public File getDefaultVenvRequirements() {
        File defaultRequirements;

        defaultRequirements = new File(tomcatPath + scriptServerResourcesPath + "/" + defaultVenvRequirementsFileName);

        if (defaultRequirements.exists()) {
            logger.info("Found default requirements in : '" + defaultRequirements.getPath() + "'");
            return defaultRequirements;
        }
        logger.error("Default requirements not found in : '" + defaultRequirements.getPath() + "'");
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

    public String[] createParamsString(Script script, Map<String, String[]> requestParamsMap, HttpServletRequest req) {
        Map<String, String[]> receivedParams = new HashMap<>(requestParamsMap); // Duplicating original map, due to map lock
        receivedParams.remove("scriptName"); // Key containing script name
        // todo refactor to array mb?
        // todo нужна ли здесь валидация
        Parameters[] configParams = stringToListOfParams(script.getParametersJson());
        ArrayList<String> resultArray = new ArrayList<String>();
        for (Parameters paramKey : configParams) {
            if (paramKey.isConstant()) {
                if (receivedParams.get(paramKey) == null) {
                    continue;
                }
                resultArray.add(paramKey.getParam());
                resultArray.add(paramKey.getDefaultConstant());
            }
            if (paramKey.getType().equals("username")) {
                resultArray.add(paramKey.getParam());
                resultArray.add(req.getRemoteUser());
            }
            if (paramKey.getType().equals("hidden")) {
                resultArray.add(paramKey.getParam());
                resultArray.add(paramKey.getDefaultConstant());
            }
            if (paramKey.getType().equals("boolean")) {
                // From ui key is received only if boolean == True, but argParser doesn't need value, only key presence
                if (receivedParams.get(paramKey.getParam()) != null) { // checking that we received values
                    logger.debug("Adding boolean param '" + paramKey.getParam() + "' to results, cause it's value was : '" + receivedParams.get(paramKey.getParam())[0] + "'");
                    resultArray.add(paramKey.getParam()); // adding key to result string
                    receivedParams.remove(paramKey.getParam()); // removing param from future iteration
                }
            }
            if (paramKey.getType().equals("fileSelect")) {
                resultArray.add(paramKey.getParam());
                resultArray.add(
                        storageService.findFileForScriptWithName(
                                receivedParams.get(paramKey.getParam())[0]
                                ,script.getName()));
                receivedParams.remove(paramKey.getParam()); // removing param from future iteration
            }
        }
        for (String paramKey : receivedParams.keySet()) {
            String[] paramValueArray = receivedParams.get(paramKey);
            String paramValue = String.join("; ", paramValueArray);
            if (paramValue.isEmpty()) { // Пропускаем username,hidden,constant
                logger.debug("Skipping not presented param '" + paramKey + "'");
                continue;
            } else {
                logger.debug("Adding to result key '" + paramKey + "'");
                logger.debug("Adding to result value '" + paramValue + "'");
                resultArray.add(paramKey);
                resultArray.add(paramValue);
            }
        }
        String[] resultString = new String[resultArray.size()];
        for (int i = 0; i < resultArray.size(); i++) {
            resultString[i] = resultArray.get(i);
        }
        return resultString;
    }

    public HashMap<RunningScript, Process> getCopyOfProcessQueue() {
        HashMap<RunningScript, Process> processMap = new HashMap<>();

        for (HashMap<RunningScript, Process> mapFromQueue : processQueueConfig.processBlockingQueue()) {
            RunningScript runningScript = (RunningScript) mapFromQueue.keySet().toArray()[0];
            processMap.put(runningScript, mapFromQueue.get(runningScript));
        }
        return processMap;
    }

    public String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
