package com.java.irembo_backend.service;

import com.java.irembo_backend.config.JwtService;
import com.java.irembo_backend.model.*;
import com.java.irembo_backend.repository.TokenRepository;
import com.java.irembo_backend.repository.UserRepository;
import com.java.irembo_backend.requests.AuthenticationRequest;
import com.java.irembo_backend.requests.RegisterRequest;
import com.java.irembo_backend.requests.ResetPasswordRequest;
import com.java.irembo_backend.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final static Integer LENGTH = 8;

    public AuthenticationResponse register(RegisterRequest request) {

        MaritalStatus status;
        if(request.getMaritalStatus().equalsIgnoreCase("SINGLE")){
            status = MaritalStatus.SINGLE;
        } else if (request.getMaritalStatus().equalsIgnoreCase("MARRIED")) {
            status = MaritalStatus.MARRIED;
        } else if (request.getMaritalStatus().equalsIgnoreCase("WIDOWED")) {
            status = MaritalStatus.WIDOWED;
        } else status = MaritalStatus.DIVORCED;

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .gender(request.getGender())
                .age(request.getAge())
                .dateOfBirth(request.getDob())
                .maritalStatus(status)
                .nationality(request.getNationality())
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder().token(jwtToken)
                .build();    }

    private void revokeAllUserTokens(User user){
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()){return;}
        validUserTokens.forEach(t ->{
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder().user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user,jwtToken);
        return AuthenticationResponse.builder().token(jwtToken)
                .build();
    }

    public static Supplier<Integer> createRandomOneTimePassword() {
        return () -> {
            SecureRandom random = new SecureRandom();
            StringBuilder oneTimePassword = new StringBuilder();
            for (int i = 0; i < LENGTH; i++) {
                int randomNumber = random.nextInt(10);
                oneTimePassword.append(randomNumber);
            }
            return Integer.parseInt(oneTimePassword.toString().trim());
        };
    }

    public String forgotPassword(ResetPasswordRequest resetPasswordRequest){
        var user = userRepository.findByEmail(resetPasswordRequest.getEmail());
        if (!user.isEmpty()){
            Integer otp = createRandomOneTimePassword().get();
            userRepository.updateOtpById(user.get().getEmail(), otp);
            // logic to send the otp to the email
        return otp.toString();
        }
    return "user not found";
    }

}
