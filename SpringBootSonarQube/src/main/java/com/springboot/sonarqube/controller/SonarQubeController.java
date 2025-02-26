package com.springboot.sonarqube.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SonarQubeController {

    @RequestMapping("/sonarqube")
    public String hello() {
        return "Hello, I'm cloud SonarQube!";
    }
}
