package com.example.springboot1.repo;


import com.example.springboot1.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Tasks ,Integer> {
    List<Tasks> findByUsers_Id(int userId);

    List<Tasks> findByUserid(int userid);


}
