package com.coronacarecard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.coronacarecard")
@EnableScheduling
@EnableJpaAuditing
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
