package com.septangle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ReggieTakeOutApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieTakeOutApplication.class,args);
        log.info("successfully started");
    }
}