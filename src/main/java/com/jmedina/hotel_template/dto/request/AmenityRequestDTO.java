package com.jmedina.hotel_template.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AmenityRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100)
    private String name;

    private String icon;
}