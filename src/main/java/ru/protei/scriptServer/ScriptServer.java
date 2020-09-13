package ru.protei.scriptServer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class ScriptServer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ScriptServer.class);
    }

    public static void main(String[] args) throws Exception {
        String configLocation = System.getProperty("spring.config.location"); //Get the default config directory
        if (configLocation == null) {
            configLocation = "classpath:/";
        }
        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(ScriptServer.class)
                .properties("spring.config.name:script-server,application", // First entry here is prioritized by Spring
                        "spring.config.location:" + "classpath:/," + configLocation) // Same as above
                .build()
                .run(args);
    }

}
