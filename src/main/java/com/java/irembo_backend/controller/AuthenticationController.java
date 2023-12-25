package com.java.irembo_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.irembo_backend.requests.AuthenticationRequest;
import com.java.irembo_backend.requests.PasswordResetRequest;
import com.java.irembo_backend.requests.RegisterRequest;
import com.java.irembo_backend.requests.ForgotPasswordRequest;
import com.java.irembo_backend.response.AuthenticationResponse;
import com.java.irembo_backend.response.PasswordResetResponse;
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
    public ResponseEntity<Object> forgotPassword(@RequestBody ForgotPasswordRequest resetPasswordRequest) throws MessagingException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        int response = authenticationService.forgotPassword(resetPasswordRequest);
        if (response == 200){
            json = objectMapper.writeValueAsString(new PasswordResetResponse(200, "Please check your email for the password reset OTP"));
            return ResponseEntity.ok().body(json);
        }else {
            json = objectMapper.writeValueAsString(new PasswordResetResponse(404, "The email is not found in our records"));
            return ResponseEntity.ok().body(json);
        }
    }

    @PostMapping("reset_password")
    public ResponseEntity<Object> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        int response = authenticationService.resetPassword(passwordResetRequest);
        if (response != 503){
            if (response != 404){
                if (response == 200) {
                    json = objectMapper.writeValueAsString(new PasswordResetResponse(200, "Password changed successfully"));
                    return ResponseEntity.ok().body(json);
                } else if (response == 405) {
                    json = objectMapper.writeValueAsString(new PasswordResetResponse(405, "Invalid Otp, Please check your email"));
                    return ResponseEntity.ok().body(json);
                }else {
                    json = objectMapper.writeValueAsString(new PasswordResetResponse(406, "Passwords don't match"));
                    return ResponseEntity.ok().body(json);
                }
            }else {
                json = objectMapper.writeValueAsString(new PasswordResetResponse(404, "Incorrect email provided"));
                return ResponseEntity.ok().body(json);
            }
        }else {
            json = objectMapper.writeValueAsString(new PasswordResetResponse(503, "User not found in our records"));
            return ResponseEntity.ok().body(json);
        }



    }
}
