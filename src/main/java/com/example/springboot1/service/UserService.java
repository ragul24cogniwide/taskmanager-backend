package com.example.springboot1.service;


import com.example.springboot1.model.Users;
import com.example.springboot1.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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

    public ResponseEntity<List>


}
