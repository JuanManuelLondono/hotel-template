package com.jmedina.hotel_template.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;
    private String website;
    private String coverImageUrl;
    private Double averageRating;
    private Integer totalReviews;
    private String checkInTime;
    private String checkOutTime;
    private String cancellationPolicy;
    private boolean active;
    private LocalDateTime createdAt;

    // Incluimos los tipos de habitación resumidos
    // para mostrarlos en la página principal del hotel
    private List<RoomTypeSummaryDTO> roomTypes;

}
