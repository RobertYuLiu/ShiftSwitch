package com.jos.dem.springboot.h2.controller;

import com.jos.dem.springboot.h2.config.AuthenticationBean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1")
public class BasicAuthController {

    @GetMapping(path = "/basicAuth")
    public AuthenticationBean basicAuth() {
        System.out.println("...enterring basic auth controller ...");
        return new AuthenticationBean("You are authenticated");
    }
}