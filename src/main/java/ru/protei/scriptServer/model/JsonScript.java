package ru.protei.scriptServer.model;

import java.util.List;

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
    public List<Parameters> parameters;
}
