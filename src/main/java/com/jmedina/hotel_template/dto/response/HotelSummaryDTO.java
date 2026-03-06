package com.jmedina.hotel_template.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelSummaryDTO {

    private Long id;
    private String name;
    private String city;
    private String country;
    private String coverImageUrl;
    private Double averageRating;
    private Integer totalReviews;
    // Precio mínimo de las habitaciones — para mostrar "desde $X"
    private java.math.BigDecimal minPrice;

}
