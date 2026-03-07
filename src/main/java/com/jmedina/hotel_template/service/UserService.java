package com.jmedina.hotel_template.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jmedina.hotel_template.dto.response.UserResponseDTO;
import com.jmedina.hotel_template.model.User;
import com.jmedina.hotel_template.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Buscar usuario por ID
    public UserResponseDTO findById(Long id){

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
        return toResponseDTO(user); 
    }
    
    // Buscar usuario por email — usado internamente por Spring Security
    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + email));
    }

    // Listar todos los admins — para el panel de gestión
    public List<UserResponseDTO> findAllAdmins(){
        return userRepository.findByRoleAndActiveTrue(User.Role.ADMIN)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Desactivar usuario — soft delete, no se elimina de la BD
    @Transactional
    public UserResponseDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
        user.setActive(false);
        return toResponseDTO(userRepository.save(user));
    }

    // Cambiar contraseña
    public void changePassword(Long userId, String currentePassword, String newPassword){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        
        if(!passwordEncoder.matches(currentePassword, user.getPassword())){
            throw new IllegalArgumentException("La contraseña actual es incorrecta");

        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Convierte entidad → DTO — método reutilizable dentro del servicio
    public UserResponseDTO toResponseDTO(User user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}