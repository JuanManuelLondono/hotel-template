package com.jmedina.hotel_template.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
    // Sin password — nunca se expone    

}
