package com.example.springboot1.service;


import com.example.springboot1.model.Users;
import com.example.springboot1.repo.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);


    public String setRegister(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        user.setConfirmpassword(encoder.encode(user.getConfirmpassword()));
        System.out.println(user.getConfirmpassword());
        repo.save(user);
        return "User registered successfully";
    }

    public ResponseEntity<List<Users>> getusers() {
        try{
            List<Users> users = repo.findAll();
            if(users.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }
            return ResponseEntity.ok(users);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    for profile
    public ResponseEntity<Users> getUserById(int id) {
        try {
            Optional<Users> user = repo.findById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //update the users to updatebyId
    public ResponseEntity<String> updateuserById(int id, Users updateuser) {
        try {
            Optional<Users> optionalUsers = repo.findById(id);
            if (optionalUsers.isPresent()) {
                Users existingUser = optionalUsers.get();

                existingUser.setUsername(updateuser.getUsername());
                existingUser.setEmailid(updateuser.getEmailid());
                existingUser.setRole(updateuser.getRole());
                existingUser.setPassword(updateuser.getPassword());

                // Check if password has changed before encoding
                if (!encoder.matches(updateuser.getPassword(), existingUser.getPassword())) {
                    String hashedPassword = encoder.encode(updateuser.getPassword());
                    existingUser.setPassword(hashedPassword);
                    existingUser.setConfirmpassword(hashedPassword);
                }

                repo.save(existingUser);
                return ResponseEntity.ok("User Updated Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user: " + e.getMessage());
        }
    }

    //update the use status in the Admin side
    @PutMapping("/updatestatus/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable int id, @RequestBody Users updateStatusRequest) {
        try {
            Optional<Users> optionalUser = repo.findById(id);
            if (optionalUser.isPresent()) {
                Users existingUser = optionalUser.get();

                // ✅ Correct field: setStatus instead of setUsername
                existingUser.setStatus(updateStatusRequest.getStatus());

                repo.save(existingUser);
                return ResponseEntity.ok("User status updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }

//    public Optional<Users> getUserWithTasks(int  userId) {
//        return repo.findById(userId);
//    }

}
