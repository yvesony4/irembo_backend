package com.java.irembo_backend.controller;

import com.java.irembo_backend.model.User;
import com.java.irembo_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/irembo/api/v1")
public class UserController {
    private final UserService userService;

    @PostMapping(path="/users")
    public ResponseEntity<Object> createUser(@RequestBody User user){
        User createdUser =  userService.createUser(user);
        if(createdUser != null)
        {
            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        }
        else
        {
            return new ResponseEntity<>("Something went wrong", HttpStatus.CREATED);
        }
    }

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
}
