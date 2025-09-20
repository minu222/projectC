package com.example.mylms01;

import org.springframework.boot.SpringApplication;

public class TestMylms01Application {

    public static void main(String[] args) {
        SpringApplication.from(DemoApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
