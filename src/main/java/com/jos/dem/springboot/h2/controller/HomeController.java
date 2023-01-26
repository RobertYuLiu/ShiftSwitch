package com.jos.dem.springboot.h2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HomeController {

    @GetMapping("/home")
    public String home(Principal principal) {
        //this will print out: Hello user  <=== the 'user' is the username used
        return "Hello, " + principal.getName();
    }

}