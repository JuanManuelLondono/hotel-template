package com.jmedina.hotel_template.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private String userName;        // nombre del autor
    private Integer rating;
    private Integer ratingCleanliness;
    private Integer ratingComfort;
    private Integer ratingLocation;
    private Integer ratingService;
    private Integer ratingValue;
    private String title;
    private String comment;
    private String hotelResponse;
    private LocalDateTime hotelResponseAt;
    private boolean verifiedStay;
    private Integer helpfulCount;
    private LocalDateTime createdAt;
}