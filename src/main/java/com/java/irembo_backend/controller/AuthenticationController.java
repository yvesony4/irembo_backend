package com.java.irembo_backend.controller;

import com.java.irembo_backend.requests.AuthenticationRequest;
import com.java.irembo_backend.requests.RegisterRequest;
import com.java.irembo_backend.requests.ResetPasswordRequest;
import com.java.irembo_backend.response.AuthenticationResponse;
import com.java.irembo_backend.service.AuthenticationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/irembo/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("forgot_password")
    public ResponseEntity<String> forgotPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws MessagingException {
        return ResponseEntity.ok(authenticationService.forgotPassword(resetPasswordRequest));
    }


}
