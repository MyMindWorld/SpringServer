package ru.protei.scriptServer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Data
@AllArgsConstructor
public class JsonScript {

    public String name;
    public String script_path;
    public String description;
    public String working_directory;
    public List<String> allowed_users;
    public List<String> output_files;
    public boolean requires_terminal;
    public boolean bash_formatting;
    public String include;
    public boolean hidden;


    public Parameters[] parameters;

    public JsonScript() {
        super();
    }

    public String paramsToJsonString(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(parameters);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
