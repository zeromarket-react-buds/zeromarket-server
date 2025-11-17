package com.zeromarket.server.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SiwonController {

    @GetMapping("/siwon")
    public Map<String, Object> siwon(){
        return Map.of("name", "siwon");
    }
}


