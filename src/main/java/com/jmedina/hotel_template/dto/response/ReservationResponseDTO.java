package com.jmedina.hotel_template.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {
    private Long id;
    private String reservationCode;

    // Info del huésped — solo lo necesario
    private Long userId;
    private String userName;
    private String userEmail;

    // Info de la habitación
    private Long roomId;
    private String roomNumber;
    private String roomTypeName;
    private String hotelName;

    // Fechas y precios
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Long totalNights;
    private BigDecimal pricePerNight;
    private BigDecimal totalPrice;
    private BigDecimal taxAmount;
    private BigDecimal finalPrice;

    private Integer guestsCount;
    private String specialRequests;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}