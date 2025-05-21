package com.example.springboot1.controller;

import com.example.springboot1.model.Tasks;
import com.example.springboot1.model.Users;
import com.example.springboot1.repo.TaskRepository;
import com.example.springboot1.repo.UserRepository;
import com.example.springboot1.service.JwtService;
import com.example.springboot1.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000") // Add this
public class TaskController {

    @Autowired
    private TasksService tasksService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository usersRepository;

    @PostMapping("createtasks")
    public ResponseEntity<String> createTasks(
            @RequestBody Tasks tasks,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.extractUserName(token);

            Users user = usersRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            return tasksService.createTaskWithUser(tasks, user);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while creating task: " + e.getMessage());
        }
    }

    //Retreiving data by particular id
    @GetMapping("/getalltasksbyid/{id}")
    public ResponseEntity<List<Tasks>> getAllTasksByUserId(
            @PathVariable int id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.extractUserName(token);

            return tasksService.getAllTasksByUserId(id);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Update the tasks
    @PutMapping("/updatetask/{id}")
    public ResponseEntity<String> updateTaskById(
            @PathVariable int id,
            @RequestBody Tasks updatedTask,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.extractUserName(token);

            return tasksService.updateTasksbyId(id, updatedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update task.");
        }
    }


    @DeleteMapping("/deletetask/{id}")
    public ResponseEntity<String> deleteTaskById(
            @PathVariable int id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.extractUserName(token);

            return tasksService.deleteTaskById(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete task");
        }
    }


    //Retreiving all the tasks for the admin
    @GetMapping("getalltasks/{id}")
    public ResponseEntity<String> getalltasks(@PathVariable int id){
        return tasksService.getalltasks(id);
    }


}
