package com.jmedina.hotel_template.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequestDTO {

    @NotNull(message = "La calificación es obligatoria")
    @Min(1) @Max(5)
    private Integer rating;

    @Min(1) @Max(5)
    private Integer ratingCleanliness;

    @Min(1) @Max(5)
    private Integer ratingComfort;

    @Min(1) @Max(5)
    private Integer ratingLocation;

    @Min(1) @Max(5)
    private Integer ratingService;

    @Min(1) @Max(5)
    private Integer ratingValue;

    @NotBlank(message = "El comentario es obligatorio")
    @Size(min = 10, max = 2000)
    private String comment;

    @Size(max = 150)
    private String title;
}