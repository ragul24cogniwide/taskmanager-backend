package com.example.springboot1.controller;


import com.example.springboot1.model.Users;
import com.example.springboot1.repo.UserRepository;
import com.example.springboot1.service.JwtService;
import com.example.springboot1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {


    @Autowired
    private UserService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository usersRepository;

    @GetMapping("csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request){
        return (CsrfToken) request.getAttribute("_csrf");
    }

    //register the new user
    @PostMapping("register")
    public String registerUser(@RequestBody Users user) {
        return service.setRegister(user);
    }


    //login to verify if the user is valid or not
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody Users user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(user.getUsername());

                // Fetch the full user to get ID
                Users loggedInUser = usersRepository.findByUsername(user.getUsername());
                if (loggedInUser == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
                }

                Map<String, Object> response = new HashMap<>();
                response.put("id", loggedInUser.getId());
                response.put("token", token);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed");
            }

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("getallusers")
    public ResponseEntity<List<Users>>getallusers(@RequestHeader("Authorization") String authHeader){
        try{
            String token = authHeader.replace("Bearer ","");
            String username = jwtService.extractUserName(token);

            return service.getusers();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //for profile
    @GetMapping("/getUserbyid/{id}")
    public ResponseEntity<Users> getUserById(
            @PathVariable int id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.extractUserName(token);

            // You can validate the username here if needed

            return service.getUserById(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
