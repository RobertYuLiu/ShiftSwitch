package com.jos.dem.springboot.h2.service;

import org.springframework.stereotype.Service;

@Service
public class TestUtilityService {

    public String getTestMessage(String name) {
        return "haha haha! " + name;
    }
}
