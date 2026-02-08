package com.myturn.MyTurn.controller;
import com.myturn.MyTurn.dto.LoginRequest;
import com.myturn.MyTurn.dto.SignupRequest;
import com.myturn.MyTurn.model.User;
import com.myturn.MyTurn.security.JwtUtil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myturn.MyTurn.repository.UserRepository;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private JwtUtil jwtutil;
    @PostMapping("/signup")
    public String register(@RequestBody SignupRequest request) {
        if(userRepository.existsByUsername(request.getUsername()) ||
                userRepository.existsByEmail(request.getEmail())){
            return "User already Exists";
        }
        User user=new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole("USER");

        userRepository.save(user);  

        return "User registered successfully!";
        
    }
    //using map to store credentials in local storage
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {

        Map<String, Object> res = new HashMap<>();
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            res.put("error", "User not found");
            return res;
        }

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            res.put("error", "Invalid password");
            return res;
        }

        String token = jwtutil.generateToken(user.getUsername());

        res.put("token", token);
        res.put("role", user.getRole());
        res.put("username", user.getUsername());

        return res;
    }   
}
