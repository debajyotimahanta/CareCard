package com.coronacarecard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.coronacarecard")
public class Application {
    private static final Logger log = LogManager.getLogger(Application.class);
    public static void main(String[] args) {
        log.info("Launching application..");
        SpringApplication.run(Application.class, args);
    }
}
