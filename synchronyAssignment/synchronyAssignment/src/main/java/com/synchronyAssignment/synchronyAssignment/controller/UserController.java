package com.synchronyAssignment.synchronyAssignment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synchronyAssignment.synchronyAssignment.entity.User;
import com.synchronyAssignment.synchronyAssignment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) throws Exception {
        User newUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created with ID: " + newUser.getId());
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id)
    {
        return userService.getUser(id);

    }
    @GetMapping("bulk/{ids}")
    public ResponseEntity<List<User>> getUserList(@PathVariable List<Long> ids) throws ExecutionException, InterruptedException {
        return userService.getBulkUsers(ids);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id,@RequestBody User user) throws JsonProcessingException {
        userService.updateUser(id,user);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
