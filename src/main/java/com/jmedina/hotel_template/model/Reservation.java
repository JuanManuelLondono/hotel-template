package com.jmedina.hotel_template.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_code", unique = true, nullable = false, length = 50)
    private String reservationCode;

    @NotNull(message = "La fecha de entrada es obligatoria")
    @Future(message = "La fecha de entrada debe ser futura")
    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Future(message = "La fecha de salida debe ser futura")
    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "total_nights", nullable = false)
    private Long totalNghts;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Builder.Default
    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    @Column(name = "guests_count", nullable = false)
    private Integer guestsCount;

    // Solicitudes especiales del cliente (cama adicional, piso alto, etc.)
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.PENDING;

    // Fecha en que se confirmó el pago
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // Fecha en que se canceló (si aplica)
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Motivo de cancelación
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -------------------------------------------------------
    // RELACIONES
    // -------------------------------------------------------

    // La reserva pertenece a un usuario
    @JsonBackReference("user-reservations")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // La reserva es para una habitación específica
    @JsonBackReference("room-reservations")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // -------------------------------------------------------
    // LÓGICA AUTOMÁTICA
    // -------------------------------------------------------

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calcularTotales();
        generarCodigo();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
        calcularTotales();
    }

    private void calcularTotales(){
        if (checkIn != null && checkOut != null && pricePerNight != null) {
            totalNghts = ChronoUnit.DAYS.between(checkIn, checkOut);

            if (totalNghts <= 0) {
                throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada");
            }
            totalPrice = pricePerNight.multiply(BigDecimal.valueOf(totalNghts));
            finalPrice = totalPrice.add(taxAmount != null ? taxAmount : BigDecimal.ZERO);
        }
    }

    private void generarCodigo(){
        if (reservationCode == null) {
            reservationCode = "RES-" + System.currentTimeMillis();
        }
    }

    public enum ReservationStatus{
        PENDING,
        CONFIRMED,
        CHECKED_IN,
        CHECKED_OUT,
        CANCELLED,
        NO_SHOW
    }

}
