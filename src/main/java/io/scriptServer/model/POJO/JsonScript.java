package io.scriptServer.model.POJO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JsonScript {

    public String name; // unique
    public String group;
    public String display_name;
    public String venv;
    public String requirements;
    public String python_version;
    public String script_path;
    public String description;
    // add display name
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

    public String paramsToJsonString() {
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
