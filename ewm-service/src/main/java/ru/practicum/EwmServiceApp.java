package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@PropertySource("classpath:application.properties")
public class EwmServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EwmServiceApp.class, args);
    }
}
