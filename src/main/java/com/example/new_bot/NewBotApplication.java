package com.example.new_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NewBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewBotApplication.class, args);
    }

}
