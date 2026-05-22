package com.seap;

import com.embabel.agent.api.annotation.EmbabelComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
public class SeapAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeapAgentApplication.class, args);
    }

}
