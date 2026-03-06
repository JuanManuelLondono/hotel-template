package com.jmedina.hotel_template.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class HotelRequestDTO {

     @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 150)
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "El país es obligatorio")
    private String country;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private Double longitude;

    private String phone;
    private String email;
    private String website;
    private String checkInTime;
    private String checkOutTime;
    private String cancellationPolicy;

}
