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

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://focus-track.vercel.app/") //for online vercel origin

public class UserController {


    @Autowired
    private UserService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository usersRepository;



//    @GetMapping("csrf-token")
//    public CsrfToken getCsrfToken(HttpServletRequest request){
//        return (CsrfToken) request.getAttribute("_csrf");
//    }

    //register the new user
    @PostMapping("register")
    public ResponseEntity<String> registerUser(@RequestBody Users user) {
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

                // Check status first
                if ("Pending".equalsIgnoreCase(loggedInUser.getStatus())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Access is still Pending");
                }


                Map<String, Object> response = new HashMap<>();
                response.put("id", loggedInUser.getId());
                response.put("token", token);
                response.put("role",loggedInUser.getRole());

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

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateuserById(
            @PathVariable int id,
            @RequestBody Users updateuser,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.extractUserName(token);

            // You can check if token user matches the updater (optional)
            // if (!username.equals(updateuser.getUsername())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            return service.updateuserById(id, updateuser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/updatestatus/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable int id, @RequestBody Users updateStatusRequest) {
        try {
            Optional<Users> optionalUser = usersRepository.findById(id);
            if (optionalUser.isPresent()) {
                Users existingUser = optionalUser.get();
                existingUser.setStatus(updateStatusRequest.getStatus());
                existingUser.setRole(updateStatusRequest.getRole());
                usersRepository.save(existingUser);
                return ResponseEntity.ok("User status updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader, Principal principal) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.extractUserName(token);
            Users user = usersRepository.findByUsername(username);

            if (user != null) {
                System.out.println("Hello from context" +user +principal.getName());
                return ResponseEntity.ok(user); // return id, username, role, etc.

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

//    @GetMapping("/{userId}/tasks")
//    public ResponseEntity<?> getUserTasks(@PathVariable int userId) {
//        Optional<Users> userOptional = service.getUserWithTasks(userId);
//        if (userOptional.isPresent()) {
//            return ResponseEntity.ok(userOptional.get().getTasks());
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//    }

}
