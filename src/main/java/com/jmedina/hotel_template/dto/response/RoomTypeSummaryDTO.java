package com.jmedina.hotel_template.dto.response;

import lombok.*;
import java.math.BigDecimal;

// Versión reducida para mostrar dentro de HotelResponseDTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeSummaryDTO {
    private Long id;
    private String name;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private String coverImageUrl;
    private Integer availableRooms;
}