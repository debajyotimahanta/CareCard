package com.coronacarecard;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest(properties = {"AWS_ARN=arn:aws:kms:us-west-1:008731829883:key/a72c4b37-325e-4254-9a9f-38592d01e0b2",
        "spring.app.forntEndBaseUrl=http://base"})
@ComponentScan("com.coronacarecard")
public class ApplicationTest {
}