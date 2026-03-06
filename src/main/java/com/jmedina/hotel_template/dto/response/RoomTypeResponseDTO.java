package com.jmedina.hotel_template.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private String coverImageUrl;
    private boolean active;
    private Long hotelId;
    private String hotelName;
    private List<AmenityResponseDTO> amenities;
    private List<GalleryResponseDTO> images;
    // Cuántas habitaciones físicas hay de este tipo
    private Integer totalRooms;
    // Cuántas están disponibles ahora mismo
    private Integer availableRooms;
}