package com.jmedina.hotel_template.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jmedina.hotel_template.dto.request.LoginRequestDTO;
import com.jmedina.hotel_template.dto.request.RegisterRequestDTO;
import com.jmedina.hotel_template.dto.response.AuthResponseDTO;
import com.jmedina.hotel_template.model.User;
import com.jmedina.hotel_template.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    // Registro de nuevo usuario
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO dto) {

        // Verificar que el email no esté en uso
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese email");
        }

        // Crear el usuario con contraseña encriptada
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) 
                .role(User.Role.GUEST) // por defecto todos se registran como GUEST
                .build();

        userRepository.save(user);

        // Generar token con el rol incluido en los claims
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("name", user.getName());

        String token = jwtService.generateToken(claims, userDetails);

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();    
    }    

    // Login
    public AuthResponseDTO login(LoginRequestDTO dto) {

        // AuthenticationManager verifica email + password automáticamente
        // Si las credenciales son incorrectas lanza BadCredentialsException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        // Si se llega aquí, las credenciales son correctas
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("name", user.getName());

        String token = jwtService.generateToken(claims, userDetails);

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
