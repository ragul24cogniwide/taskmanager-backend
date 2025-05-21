package com.example.springboot1.service;


import com.example.springboot1.model.Tasks;
import com.example.springboot1.model.Users;
import com.example.springboot1.repo.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TasksService {

    @Autowired
    private TaskRepository repo;


    //create a tasks by  user
    public ResponseEntity<String> createTaskWithUser(Tasks tasks, Users user) {
        try {
            tasks.setUsers(user); // attach user to the task
            Tasks savedTask = repo.save(tasks);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Task created successfully for userId: " + savedTask.getUsers().getId());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while saving task: " + e.getMessage());
        }
    }



    //Retreiving data by particular id
    public ResponseEntity<List<Tasks>> getAllTasksByUserId(int userId) {
        try {
            List<Tasks> tasks = repo.findByUsers_Id(userId);
            if (tasks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //update tasks by Id
    public ResponseEntity<String> updateTasksbyId(int id, Tasks updatedTask) {
        try {
            Optional<Tasks> optionalTask = repo.findById(id);
            if (optionalTask.isPresent()) {
                Tasks existingTask = optionalTask.get();

                // Update fields
                existingTask.setTitle(updatedTask.getTitle());
                existingTask.setDescription(updatedTask.getDescription());
                existingTask.setCategory(updatedTask.getCategory());
                existingTask.setPriority(updatedTask.getPriority());
                existingTask.setStatus(updatedTask.getStatus());
                existingTask.setDueDate(updatedTask.getDueDate());

                repo.save(existingTask);
                return ResponseEntity.ok("Task updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating task.");
        }
    }


    //delete by Id
    public ResponseEntity<String> deleteTaskById(int id) {
        try {
            Optional<Tasks> optionalTask = repo.findById(id);
            if (optionalTask.isPresent()) {
                repo.deleteById(id);
                return ResponseEntity.ok("Task deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting task.");
        }
    }



    //posting the tasks for the individual users
//    public ResponseEntity<String> createtasks (Tasks tasks){
//        try{
//            Tasks savedTask = repo.save(tasks);
//            return ResponseEntity
//                    .status(HttpStatus.CREATED)
//                    .body("Task created Successfully for userId:" + savedTask.getUser().getId());
//        }catch (Exception e){
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error while creating task" +e.getMessage());
//        }
//    }


//    public ResponseEntity<String> getAllTasksByUserId(int userId) {
//        try {
//            List<Tasks> tasks = repo.findByUsers_Id(userId); // ✅ correct usage
//            System.out.println(tasks);
//            if (tasks.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No tasks found for user ID: " + userId);
//            }
//            return ResponseEntity.ok("Tasks fetched successfully: " + tasks);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error retrieving tasks: " + e.getMessage());
//        }
//    }


    //Rertreivng all the tasks for the admin
    public ResponseEntity<String> getalltasks(int id) {
        try{
            List<Tasks> getalltask = repo.findAll();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Data Fetched Successfullly for the Individual User" +getalltask);

        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error Retreiving tasks for a individual users" +e.getMessage());
        }
    }
}
