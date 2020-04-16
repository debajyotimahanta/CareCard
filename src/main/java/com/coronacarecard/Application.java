package com.coronacarecard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.coronacarecard")
@EnableScheduling
public class Application {
    private static final Logger log = LogManager.getLogger(Application.class);
    public static void main(String[] args) {
        log.info("Launching application..");
        SpringApplication.run(Application.class, args);
    }
}
