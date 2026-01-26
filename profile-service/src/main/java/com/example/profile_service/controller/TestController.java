package com.example.profile_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/main")
public class TestController {
    @Value("${eureka.instance.instance-id}")
    private String value;

    @GetMapping("/test")
    public String test() {
        return value;
    }
}
