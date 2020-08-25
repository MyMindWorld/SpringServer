package ru.protei.scriptServer.model.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutrackIssuesResponse {
    public String idReadable;
    public String summary;
}
