package com.myturn.MyTurn.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;  

@RestController
public class TestController {
    
    @GetMapping
    public String home(){
        return "MyTurn Backend is Running!";
    }
    @GetMapping("/test")
    public String test(){
        return "API is working!";
    }
}
