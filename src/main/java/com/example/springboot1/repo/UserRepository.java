package com.example.springboot1.repo;

import com.example.springboot1.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {


    Users findByUsername(String username);

    // Native SQL query to find user by email
    @Query(value = "SELECT * FROM users WHERE emailid = ?1", nativeQuery = true)
    Users findByEmail(String emailid);

//    @Query(value = "SELECT * FROM users WHERE id = ?1", nativeQuery = true)
//    Users getUserById(int id);


}
