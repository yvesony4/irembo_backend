package com.java.irembo_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/irembo/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    @GetMapping
    public ResponseEntity<String> helloWorld(){
        return ResponseEntity.ok("Hello from secured endpoint");
    }
}
