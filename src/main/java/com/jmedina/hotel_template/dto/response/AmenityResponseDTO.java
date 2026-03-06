package com.jmedina.hotel_template.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityResponseDTO {
    private Long id;
    private String name;
    private String icon;
}