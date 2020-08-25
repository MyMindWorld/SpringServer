package ru.protei.scriptServer.planner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.scriptServer.model.POJO.YoutrackIssuesResponse;

import java.util.Optional;

import static ru.protei.scriptServer.utils.Utils.getUsername;

@Controller
public class PlannerController {
    Logger logger = LoggerFactory.getLogger(PlannerController.class);
    String defaultRequestString = "Состояние: New,Discuss,Active,Review,Test,Paused,Open";

    @SneakyThrows
    public YoutrackIssuesResponse[] getIssuesNames(String userToSearch, String requestString) {
        String bearerToken = "perm:c3VwcG9ydA==.NjEtMzk=.3uX15I2FE77MOXB55mXj7mFgQOfSyt"; // Support token. Todo to props
        String requestUrl = String.format("https://youtrack.protei.ru/api/issues?fields=idReadable,summary&query=(Исполнитель: %s или Рецензент: %s) и " + requestString, userToSearch, userToSearch);
        RestAssured.defaultParser = Parser.JSON;
        String issuesStr = RestAssured.given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .urlEncodingEnabled(true)
                .get(requestUrl)
                .then()
                .extract()
                .asString();
        // todo log
        ObjectMapper jsonMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        YoutrackIssuesResponse[] issues = jsonMapper.readValue(issuesStr, YoutrackIssuesResponse[].class);
        return issues;
    }

    @RequestMapping("/Planner")
    public ModelAndView plannerPage(@RequestParam Optional<String> youtrackIssuesSearchTerm) {
        String requestString = "";
        if (youtrackIssuesSearchTerm.isPresent()) {
            requestString = youtrackIssuesSearchTerm.get();

        } else {
            requestString = defaultRequestString;
        }

        return new ModelAndView("Planner").addObject("issues", getIssuesNames(getUsername(), requestString)).addObject("searchQuery", requestString);
    }
}
