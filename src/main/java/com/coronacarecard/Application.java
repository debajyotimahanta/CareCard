package com.coronacarecard;

import com.coronacarecard.dao.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.coronacarecard")
public class Application {
    @Autowired
    private BusinessRepository businessRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
