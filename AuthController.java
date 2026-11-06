package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService usuarioDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.get("email"), request.get("password"))
        );
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(request.get("email"));
        String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("token", jwt));
    }
}