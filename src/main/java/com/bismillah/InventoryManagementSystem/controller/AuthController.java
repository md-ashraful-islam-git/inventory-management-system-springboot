package com.bismillah.InventoryManagementSystem.controller;

import com.bismillah.InventoryManagementSystem.dto.LoginRequest;
import com.bismillah.InventoryManagementSystem.dto.RegisterRequest;
import com.bismillah.InventoryManagementSystem.dto.Response;
import com.bismillah.InventoryManagementSystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {




    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody @Valid RegisterRequest registerRequest){
        return ResponseEntity.ok(userService.registerUser(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@RequestBody @Valid LoginRequest loginRegister){
        return ResponseEntity.ok(userService.loginUser(loginRegister));
    }
}
