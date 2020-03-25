package com.coronacarecard;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest(properties="spring.app.forntEndBaseUrl=http://base")
@ComponentScan("com.coronacarecard")
public class ApplicationTest {
}