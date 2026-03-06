package com.jmedina.hotel_template.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationRequestDTO {

    @NotNull(message = "El tipo de habitación es obligatorio")
    private Long roomTypeId;

    @NotNull(message = "La fecha de entrada es obligatoria")
    @Future(message = "La fecha de entrada debe ser futura")
    private LocalDate checkIn;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Future(message = "La fecha de salida debe ser futura")
    private LocalDate checkOut;

    @NotNull(message = "El número de huéspedes es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    private Integer guestsCount;

    private String specialRequests;
}