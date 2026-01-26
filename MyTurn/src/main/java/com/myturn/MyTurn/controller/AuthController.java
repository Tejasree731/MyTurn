package com.myturn.MyTurn.controller;
import com.myturn.MyTurn.dto.LoginRequest;
import com.myturn.MyTurn.dto.SignupRequest;
import com.myturn.MyTurn.model.User;
import com.myturn.MyTurn.security.JwtUtil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myturn.MyTurn.repository.UserRepository;

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
        user.setRole("User");
        userRepository.save(user);  

        return "User registered successfully!";
        
    }

    @PostMapping("login")
    public String login(@RequestBody LoginRequest request) {
        User user=userRepository.findByUsername(request.getUsername())
                    .orElse(null);
        if(user==null){
            return "User not found!";
        }
        if(!encoder.matches(request.getPassword(),user.getPassword())){
            System.out.println(request.getPassword());
            return "Incorrect password!";
        }
        String token=jwtutil.generateToken(user.getUsername());
        return "Login Successful\nToken: "+token;
    }
    
    
}
