package com.jmedina.hotel_template.controller;

import com.jmedina.hotel_template.dto.request.LoginRequestDTO;
import com.jmedina.hotel_template.dto.request.RegisterRequestDTO;
import com.jmedina.hotel_template.dto.response.ApiResponseDTO;
import com.jmedina.hotel_template.dto.response.AuthResponseDTO;
import com.jmedina.hotel_template.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO dto) {

        AuthResponseDTO response = authService.register(dto);
        return ResponseEntity.ok(ApiResponseDTO.ok(response, "Registro exitoso"));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO dto) {

        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(ApiResponseDTO.ok(response, "Login exitoso"));
    }
}