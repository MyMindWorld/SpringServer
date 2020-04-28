package ru.protei.scriptServer.model.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class GitlabGroupsAnswer {

    @JsonProperty("http_url_to_repo")
    public String httpUrlToRepo;
    @JsonProperty("name")
    public String name;
}
