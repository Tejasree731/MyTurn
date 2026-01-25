package com.myturn.MyTurn.controller;
import com.myturn.MyTurn.dto.LoginRequest;
import com.myturn.MyTurn.dto.SignupRequest;
import com.myturn.MyTurn.model.User;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myturn.MyTurn.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/signup")
    public String register(@RequestBody SignupRequest request) {
        if(userRepository.existsByUsername(request.getUsername()) ||
                userRepository.existsByEmail(request.getEmail())){
            return "User already Exists";
        }
        User user=new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole("User");
        userRepository.save(user);  

        return "User registered successfully!";
        
    }

    @PostMapping("login")
    public String login(@RequestBody LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
            .filter(user -> user.getPassword().equals(request.getPassword()))
            .map(user -> "Login successful! Role: " + user.getRole())
            .orElse("Invalid username or password");
    }
    
    
}
