package ru.protei.scriptServer.model.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;

@Data
public class Parameters {
    public String name;
    public String param;
    public String script;
    //    public boolean filter_by_search_select = false;
    public boolean no_value;
    public String description;
    public boolean required;
    public boolean constant;

    @JsonProperty("default")
    public String defaultConstant;

    public String type;
    public boolean secure;
    public String max;
    public String min;
    public String file_dir;
    public String[] file_extensions;
    public boolean multiple_arguments;
    public boolean file_recursive;
    public String[] values;

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
