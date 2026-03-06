package com.jmedina.hotel_template.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    
    private String token;           // JWT token
    private String tokenType;       // siempre "Bearer"
    private Long userId;
    private String name;
    private String email;
    private String role;
}
