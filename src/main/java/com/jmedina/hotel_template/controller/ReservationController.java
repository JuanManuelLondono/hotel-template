package com.jmedina.hotel_template.controller;

import com.jmedina.hotel_template.dto.request.ReservationRequestDTO;
import com.jmedina.hotel_template.dto.response.ApiResponseDTO;
import com.jmedina.hotel_template.dto.response.ReservationResponseDTO;
import com.jmedina.hotel_template.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final com.jmedina.hotel_template.service.UserService userService;

    // GET /api/reservations/my — usuario autenticado ve sus reservas
    @GetMapping("/my")
    public ResponseEntity<ApiResponseDTO<List<ReservationResponseDTO>>> getMyReservations(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(
            ApiResponseDTO.ok(reservationService.findByUser(userId), "Reservas obtenidas")
        );
    }

    // GET /api/reservations/hotel/{hotelId} — solo ADMIN
    @GetMapping("/hotel/{hotelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<ReservationResponseDTO>>> getByHotel(
            @PathVariable Long hotelId) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(reservationService.findByHotel(hotelId), "Reservas del hotel obtenidas")
        );
    }

    // GET /api/reservations/code/{code} — público para que el cliente consulte su reserva
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponseDTO<ReservationResponseDTO>> getByCode(
            @PathVariable String code) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(reservationService.findByCode(code), "Reserva encontrada")
        );
    }

    // POST /api/reservations — usuario autenticado crea reserva
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ReservationResponseDTO>> create(
            @Valid @RequestBody ReservationRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        ReservationResponseDTO created = reservationService.create(userId, dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponseDTO.ok(created, "Reserva creada exitosamente"));
    }

    // PATCH /api/reservations/{id}/cancel — usuario cancela su propia reserva
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponseDTO<ReservationResponseDTO>> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(
            ApiResponseDTO.ok(
                reservationService.cancel(id, userId, reason),
                "Reserva cancelada"
            )
        );
    }

    // PATCH /api/reservations/{id}/confirm-payment — solo ADMIN
    @PatchMapping("/{id}/confirm-payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ReservationResponseDTO>> confirmPayment(
            @PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(reservationService.confirmPayment(id), "Pago confirmado")
        );
    }

    // Obtiene el userId a partir del email del token JWT
    private Long getUserId(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername()).getId();
    }
}